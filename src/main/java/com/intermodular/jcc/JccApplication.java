package com.intermodular.jcc;

import com.intermodular.jcc.config.RestConfig;
import com.intermodular.jcc.entities.*;
import com.intermodular.jcc.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableConfigurationProperties(RestConfig.class)
public class JccApplication {

    public static void main(String[] args) {
        SpringApplication.run(JccApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(
            UsuarioRepository usuarioRepo,
            DepartamentoRepository deptRepo,
            HorarioRepository horarioRepo,
            FaltaRepository faltaRepo,
            CalificacionRepository calificacionRepo,
            AvisoRepository avisoRepo,
            RegistroAccesoRepository registroRepo,
            PasswordEncoder encoder) {
        return args -> {
            System.out.println("⏳ INICIANDO CARGA MASIVA DE DATOS DEMO...");

            // 1. LIMPIEZA TOTAL
            usuarioRepo.deleteAll();
            deptRepo.deleteAll();
            horarioRepo.deleteAll();
            faltaRepo.deleteAll();
            calificacionRepo.deleteAll();
            avisoRepo.deleteAll();
            registroRepo.deleteAll();

            // 2. CREAR DEPARTAMENTOS
            Departamento depInfo = new Departamento("Informática");
            Departamento depLengua = new Departamento("Lengua");
            Departamento depMat = new Departamento("Matemáticas");
            Departamento depHist = new Departamento("Historia");
            deptRepo.saveAll(Arrays.asList(depInfo, depLengua, depMat, depHist));

            // 3. CREAR PROFESORES (5 Profesores)
            List<Usuario> listaProfesores = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                Usuario profe = new Usuario();
                profe.setNombre("Profesor" + i);
                profe.setApellidos("Docente" + i);
                profe.setDni(i + "0000000P"); // DNI: 10000000P, 20000000P...
                profe.setGmail("profe" + i + "@instituto.com");
                profe.setFechaNacimiento(LocalDate.of(1980, 1, 1));
                profe.setRol(Rol.PROFESOR);
                profe.setNfcToken("NFC_PROFE_" + i);
                profe.setBaja(false);
                profe.setDepartamento(i % 2 == 0 ? depLengua : depInfo); // Alternar dept
                profe.setPassword(encoder.encode("1234"));
                listaProfesores.add(profe);
            }
            usuarioRepo.saveAll(listaProfesores);
            Usuario director = listaProfesores.get(0); // El primer profe será "Director" para los avisos

            // 4. CREAR ALUMNOS (50 Alumnos)
            List<Usuario> listaAlumnos = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                Usuario alumno = new Usuario();
                alumno.setNombre("Alumno" + i);
                alumno.setApellidos("Estudiante" + i);
                // DNI pares para 2DAM, impares para 1DAM (para recordar claves facil)
                String dni = (20000000 + i) + "A"; 
                alumno.setDni(dni);
                alumno.setGmail("alumno" + i + "@gmail.com");
                alumno.setFechaNacimiento(LocalDate.of(2005, (i % 12) + 1, 15));
                alumno.setRol(Rol.ALUMNO);
                alumno.setNfcToken("NFC_ALU_" + i);
                // Mitad a 1DAM, Mitad a 2DAM
                alumno.setCurso(i <= 25 ? "1DAM" : "2DAM"); 
                alumno.setBaja(i > 48); // Los 2 últimos están dados de baja (expulsados)
                alumno.setPassword(encoder.encode("alumno"));
                listaAlumnos.add(alumno);
            }
            usuarioRepo.saveAll(listaAlumnos);

            // 5. GENERAR HORARIOS (Semana completa)
            crearHorarioSemana(horarioRepo, "1DAM", Arrays.asList("Programación", "Sistemas", "Bases de Datos", "Entornos", "FOL"));
            crearHorarioSemana(horarioRepo, "2DAM", Arrays.asList("Acceso Datos", "Interfaces", "Android", "Procesos", "Empresa"));

            // 6. GENERAR FALTAS Y CALIFICACIONES (Datos Masivos)
            Random rand = new Random();
            List<String> asignaturas2DAM = Arrays.asList("Acceso Datos", "Interfaces", "Android", "Procesos", "Empresa");
            List<String> asignaturas1DAM = Arrays.asList("Programación", "Sistemas", "Bases de Datos", "Entornos", "FOL");

            for (Usuario alu : listaAlumnos) {
                // A. GENERAR FALTAS (Aleatorio últimos 30 días)
                for (int d = 0; d < 30; d++) {
                    if (rand.nextDouble() < 0.1) { // 10% probabilidad de falta por día
                        LocalDate fechaFalta = LocalDate.now().minusDays(d);
                        String asig = alu.getCurso().equals("2DAM") ? "Android" : "Programación";
                        boolean retraso = rand.nextBoolean();
                        
                        Falta falta = new Falta(alu, asig, fechaFalta, retraso, retraso ? "Se durmió" : "No vino");
                        if (rand.nextBoolean()) falta.setJustificada(true); // 50% justificadas
                        faltaRepo.save(falta);
                    }
                }

                // B. GENERAR NOTAS (Para todas las asignaturas)
                List<String> susAsignaturas = alu.getCurso().equals("2DAM") ? asignaturas2DAM : asignaturas1DAM;
                for (String asig : susAsignaturas) {
                    double nota = Math.round((rand.nextDouble() * 10) * 100.0) / 100.0; // Nota con 2 decimales
                    Calificacion cal = new Calificacion(alu, asig, "1ª Evaluación", nota, nota < 5 ? "Necesita mejorar" : "Buen trabajo");
                    calificacionRepo.save(cal);
                }
                
                // C. GENERAR REGISTROS ACCESO (Simulando entrada hoy)
                RegistroAcceso reg = new RegistroAcceso(LocalDateTime.now().minusHours(rand.nextInt(4)), !alu.isBaja(), alu.isBaja() ? "BLOQUEADO" : "ENTRADA", alu);
                registroRepo.save(reg);
            }

            // 7. GENERAR AVISOS
            Aviso aviso1 = new Aviso("Bienvenida", "Bienvenidos al curso 2025/2026", "TODOS", director);
            Aviso aviso2 = new Aviso("Huelga Transporte", "Se justifican retrasos mañana", "TODOS", director);
            Aviso aviso3 = new Aviso("Examen Android", "El examen será en el aula 202", "2DAM", listaProfesores.get(1));
            Aviso aviso4 = new Aviso("Excursión", "Traed autorización firmada", "1DAM", listaProfesores.get(2));
            avisoRepo.saveAll(Arrays.asList(aviso1, aviso2, aviso3, aviso4));

            System.out.println("✅ CARGA MASIVA COMPLETADA.");
            System.out.println("📊 RESUMEN:");
            System.out.println("   - Usuarios: " + usuarioRepo.count());
            System.out.println("   - Horarios: " + horarioRepo.count());
            System.out.println("   - Faltas: " + faltaRepo.count());
            System.out.println("   - Notas: " + calificacionRepo.count());
            System.out.println("   - Avisos: " + avisoRepo.count());
            System.out.println("🔑 CREDENCIALES DEMO:");
            System.out.println("   - Profe: 10000000P / 1234");
            System.out.println("   - Alumno 1DAM: 20000001A / alumno");
            System.out.println("   - Alumno 2DAM: 20000026A / alumno");
        };
    }

    // Método auxiliar para generar horario de lunes a viernes
    private void crearHorarioSemana(HorarioRepository repo, String curso, List<String> asignaturas) {
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        List<Horario> horarios = new ArrayList<>();
        
        for (String dia : dias) {
            // Generar 3 tramos por día (Ej: 8-9, 9-10, 10-11)
            // Usamos asignaturas rotativas para rellenar
            horarios.add(new Horario(curso, asignaturas.get(0), dia, LocalTime.of(8, 0), LocalTime.of(9, 0)));
            horarios.add(new Horario(curso, asignaturas.get(1), dia, LocalTime.of(9, 0), LocalTime.of(10, 0)));
            horarios.add(new Horario(curso, asignaturas.get(2), dia, LocalTime.of(10, 0), LocalTime.of(11, 0)));
            // Recreo
            horarios.add(new Horario(curso, "RECREO", dia, LocalTime.of(11, 0), LocalTime.of(11, 30)));
            // Dos clases más
            horarios.add(new Horario(curso, asignaturas.get(3), dia, LocalTime.of(11, 30), LocalTime.of(12, 30)));
            horarios.add(new Horario(curso, asignaturas.get(4), dia, LocalTime.of(12, 30), LocalTime.of(13, 30)));
        }
        repo.saveAll(horarios);
    }
}
