package com.intermodular.jcc;

import com.intermodular.jcc.entities.*;
import com.intermodular.jcc.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalTime;
import java.util.Arrays;

@SpringBootApplication
public class JccApplication {

    public static void main(String[] args) {
        SpringApplication.run(JccApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepo,
                               DepartamentoRepository deptRepo, // Necesitas crear este repo (te lo pongo abajo)
                               HorarioRepository horarioRepo) {
        return args -> {
            System.out.println("INICIANDO CARGA DE DATOS DE PRUEBA EN MONGODB...");

            usuarioRepo.deleteAll();
            deptRepo.deleteAll();
            horarioRepo.deleteAll();

            Departamento depInfo = new Departamento("Informática");
            Departamento depLengua = new Departamento("Lengua");
            deptRepo.saveAll(Arrays.asList(depInfo, depLengua));

            Usuario profe = new Usuario();
            profe.setNombre("Profesor");
            profe.setApellidos("Xavier");
            profe.setRol(Rol.PROFESOR);
            profe.setNfcToken("PROFE1");
            profe.setExpulsado(false);
            profe.setVinculadoWebFamilia(true); // Irrelevante para profes, pero lo ponemos
            profe.setDepartamento(depInfo); 
            usuarioRepo.save(profe);

            Usuario alumno = new Usuario();
            alumno.setNombre("Peter");
            alumno.setApellidos("Parker");
            alumno.setRol(Rol.ALUMNO);
            alumno.setNfcToken("ALUMNO1");
            alumno.setCurso("2DAM");
            alumno.setExpulsado(false);
            alumno.setVinculadoWebFamilia(true);
            usuarioRepo.save(alumno);

            Usuario alumnoSinPapeles = new Usuario();
            alumnoSinPapeles.setNombre("Harry");
            alumnoSinPapeles.setApellidos("Osborn");
            alumnoSinPapeles.setRol(Rol.ALUMNO);
            alumnoSinPapeles.setNfcToken("ALUMNO2");
            alumnoSinPapeles.setCurso("2DAM");
            alumnoSinPapeles.setExpulsado(false);
            alumnoSinPapeles.setVinculadoWebFamilia(false); // <--- ERROR AQUÍ
            usuarioRepo.save(alumnoSinPapeles);

            // 4. CREAR HORARIOS (Para probar ahora mismo)
            // Creamos un horario para 2DAM que dure TODO EL DÍA (00:00 a 23:59)
            // Así cuando pruebes siempre te dejará entrar por horario.
            // OJO: Cambia "VIERNES" por el día que sea hoy cuando pruebes.
            Horario horarioHoy = new Horario("2DAM", "VIERNES", LocalTime.MIN, LocalTime.MAX);
            horarioRepo.save(horarioHoy);

            System.out.println("DATOS CARGADOS CORRECTAMENTE EN MONGODB");
            System.out.println("Prueba con el token: ALUMNO1");
        };
    }
}