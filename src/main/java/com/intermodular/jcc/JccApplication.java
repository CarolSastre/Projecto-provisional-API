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

            // 1. LIMPIEZA TOTAL (Orden inverso para evitar problemas de FK si las hubiera)
            registroRepo.deleteAll();
            avisoRepo.deleteAll();
            calificacionRepo.deleteAll();
            faltaRepo.deleteAll();
            horarioRepo.deleteAll();
            usuarioRepo.deleteAll();
            deptRepo.deleteAll();

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
                profe.setDni(i + "0000000P"); // Ej: 10000000P
                profe.setGmail("profe" + i + "@instituto.com");
                profe.setFechaNacimiento(LocalDate.of(1980, 1, 1));
                profe.setRol(Rol.PROFESOR);
                profe.setNfcToken("NFC_PROFE_" + i);
                profe.setBaja(false);
                profe.setDepartamento(i % 2 == 0 ? depLengua : depInfo);
                profe.setPassword(encoder.encode("1234")); // Contraseña igual para todos
                listaProfesores.add(profe);
            }
            usuarioRepo.saveAll(listaProfesores);
            Usuario director = listaProfesores.get(0);

            // 4. CREAR ALUMNOS (50 Alumnos)
            List<Usuario> listaAlumnos = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                Usuario alumno = new Usuario();
                alumno.setNombre("Alumno" + i);
                alumno.setApellidos("Estudiante" + i);
                // DNI pares para 2DAM, impares para 1DAM
                // Generamos DNIs del tipo 20000001A, 20000002A...
                String dni = (20000000 + i) + "A"; 
                alumno.setDni(dni);
                alumno.setGmail("alumno" + i + "@gmail.com");
                alumno.setFechaNacimiento(LocalDate.of(2005, (i % 12) + 1, 15));
                alumno.setRol(Rol.ALUMNO);
                alumno.setNfcToken("NFC_ALU_" + i);
                // Mitad a 1DAM, Mitad a 2DAM
                alumno.setCurso(i <= 25 ? "1DAM" : "2DAM"); 
                // Los últimos 2 alumnos estarán de baja
                alumno.setBaja(i > 48); 
                alumno.setPassword(encoder.encode("alumno"));
                listaAlumnos.add(alumno);
            }
            usuarioRepo.saveAll(listaAlumnos);

            // 5. GENERAR HORARIOS (Semana completa)
            crearHorarioSemana(horarioRepo, "1DAM", Arrays.asList("Programación", "Sistemas", "Bases de Datos", "Entornos", "FOL"));
            crearHorarioSemana(horarioRepo, "2DAM", Arrays.asList("Acceso Datos", "Interfaces", "Android", "Procesos", "Empresa"));

            // 6. GENERAR FALTAS, CALIFICACIONES Y REGISTROS
            Random rand = new Random();
            List<String> asignaturas2DAM = Arrays.asList("Acceso Datos", "Interfaces", "Android", "Procesos", "Empresa");
            List<String> asignaturas1DAM = Arrays.asList("Programación", "Sistemas", "Bases de Datos", "Entornos", "FOL");

            for (Usuario alu : listaAlumnos) {
                // A. FALTAS (Aleatorio últimos 30 días)
                for (int d = 0; d < 30; d++) {
                    if (rand.nextDouble() < 0.1) { // 10% probabilidad de falta
                        LocalDate fechaFalta = LocalDate.now().minusDays(d);
                        String asig = alu.getCurso().equals("2DAM") ? "Android" : "Programación";
                        boolean retraso = rand.nextBoolean();
                        
                        Falta falta = new Falta(alu, asig, fechaFalta, retraso, retraso ? "Se durmió" : "No vino");
                        if (rand.nextBoolean()) falta.setJustificada(true);
                        faltaRepo.save(falta);
                    }
                }

                // B. CALIFICACIONES (Todas las asignaturas)
                List<String> susAsignaturas = alu.getCurso().equals("2DAM") ? asignaturas2DAM : asignaturas1DAM;
                for (String asig : susAsignaturas) {
                    // Nota aleatoria entre 3.0 y 10.0
                    double nota = 3.0 + (rand.nextDouble() * 7.0);
                    nota = Math.round(nota * 100.0) / 100.0; // Redondear 2 decimales
                    
                    Calificacion cal = new Calificacion(alu, asig, "1ª Evaluación", nota, nota < 5 ? "Necesita mejorar" : "Buen trabajo");
                    calificacionRepo.save(cal);
                }
                
                // C. REGISTROS DE ACCESO (Simulando hoy)
                // Generamos un registro de hace unas horas
                LocalDateTime horaEntrada = LocalDateTime.now().minusHours(rand.nextInt(4) + 1);
                boolean permitido = !alu.isBaja();
                String motivo = alu.isBaja() ? "BLOQUEADO POR BAJA" : "ENTRADA CORRECTA";
                
                RegistroAcceso reg = new RegistroAcceso(horaEntrada, permitido, motivo, alu);
                registroRepo.save(reg);
            }

            // 7. GENERAR AVISOS GLOBALES Y POR CURSO
            Aviso aviso1 = new Aviso("Bienvenida", "Bienvenidos al curso 2025/2026", "TODOS", director);
            Aviso aviso2 = new Aviso("Huelga Transporte", "Se justifican retrasos mañana por la huelga de metro.", "TODOS", director);
            Aviso aviso3 = new Aviso("Examen Android", "El examen será en el aula 202. Traed el portátil.", "2DAM", listaProfesores.get(1));
            Aviso aviso4 = new Aviso("Excursión", "Traed autorización firmada para la visita técnica.", "1DAM", listaProfesores.get(2));
            avisoRepo.saveAll(Arrays.asList(aviso1, aviso2, aviso3, aviso4));

            System.out.println("✅ CARGA MASIVA COMPLETADA.");
            System.out.println("📊 RESUMEN DE DATOS:");
            System.out.println("   - Usuarios: " + usuarioRepo.count());
            System.out.println("   - Horarios: " + horarioRepo.count());
            System.out.println("   - Faltas: " + faltaRepo.count());
            System.out.println("   - Notas: " + calificacionRepo.count());
            System.out.println("   - Avisos: " + avisoRepo.count());
            System.out.println("🔑 CREDENCIALES PARA PRUEBAS:");
            System.out.println("   - Director/Profe: 10000000P / 1234");
            System.out.println("   - Alumno 1DAM: 20000001A / alumno");
            System.out.println("   - Alumno 2DAM: 20000026A / alumno");
        };
    }

    // Método auxiliar para generar bloques horarios
    private void crearHorarioSemana(HorarioRepository repo, String curso, List<String> asignaturas) {
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        List<Horario> horarios = new ArrayList<>();
        
        for (String dia : dias) {
            // 08:00 - 09:00
            horarios.add(new Horario(curso, asignaturas.get(0), dia, LocalTime.of(8, 0), LocalTime.of(9, 0)));
            // 09:00 - 10:00
            horarios.add(new Horario(curso, asignaturas.get(1), dia, LocalTime.of(9, 0), LocalTime.of(10, 0)));
            // 10:00 - 11:00
            horarios.add(new Horario(curso, asignaturas.get(2), dia, LocalTime.of(10, 0), LocalTime.of(11, 0)));
            
            // 11:00 - 11:30 (RECREO)
            horarios.add(new Horario(curso, "RECREO", dia, LocalTime.of(11, 0), LocalTime.of(11, 30)));
            
            // 11:30 - 12:30
            horarios.add(new Horario(curso, asignaturas.get(3), dia, LocalTime.of(11, 30), LocalTime.of(12, 30)));
            // 12:30 - 13:30
            horarios.add(new Horario(curso, asignaturas.get(4), dia, LocalTime.of(12, 30), LocalTime.of(13, 30)));
        }
        repo.saveAll(horarios);
    }
}