package com.intermodular.jcc;

import com.intermodular.jcc.entities.*;
import com.intermodular.jcc.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.Arrays;

// SIN EXCLUDES NI COSAS RARAS, SOLO ESTO:
@SpringBootApplication
public class JccApplication {

    public static void main(String[] args) {
        SpringApplication.run(JccApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepo,
                               DepartamentoRepository deptRepo,
                               HorarioRepository horarioRepo,
                               PasswordEncoder encoder) { 
        return args -> {
            System.out.println("⏳ INICIANDO CARGA DE DATOS...");

            usuarioRepo.deleteAll();
            deptRepo.deleteAll();
            horarioRepo.deleteAll();

            // -- CREAR DEPARTAMENTOS --
            Departamento depInfo = new Departamento("Informática");
            Departamento depLengua = new Departamento("Lengua");
            deptRepo.saveAll(Arrays.asList(depInfo, depLengua));

            // -- CREAR USUARIOS --
            
            // 1. PROFESOR (Login: profe / 1234)
            Usuario profe = new Usuario();
            profe.setNombre("Profesor");
            profe.setApellidos("Xavier");
            profe.setRol(Rol.PROFESOR);
            profe.setNfcToken("PROFE1");
            profe.setExpulsado(false);
            profe.setVinculadoWebFamilia(true);
            profe.setDepartamento(depInfo);
            profe.setUsername("profe");
            profe.setPassword(encoder.encode("1234")); 
            usuarioRepo.save(profe);

            // 2. ALUMNO (Login: peter / spiderman)
            Usuario alumno = new Usuario();
            alumno.setNombre("Peter");
            alumno.setApellidos("Parker");
            alumno.setRol(Rol.ALUMNO);
            alumno.setNfcToken("ALUMNO1");
            alumno.setCurso("2DAM");
            alumno.setExpulsado(false);
            alumno.setVinculadoWebFamilia(true);
            alumno.setUsername("peter");
            alumno.setPassword(encoder.encode("spiderman")); 
            usuarioRepo.save(alumno);

            // 3. ALUMNO SIN PAPELES (Login: harry / duende)
            Usuario alumnoSinPapeles = new Usuario();
            alumnoSinPapeles.setNombre("Harry");
            alumnoSinPapeles.setApellidos("Osborn");
            alumnoSinPapeles.setRol(Rol.ALUMNO);
            alumnoSinPapeles.setNfcToken("ALUMNO2");
            alumnoSinPapeles.setCurso("2DAM");
            alumnoSinPapeles.setExpulsado(false);
            alumnoSinPapeles.setVinculadoWebFamilia(false);
            alumnoSinPapeles.setUsername("harry");
            alumnoSinPapeles.setPassword(encoder.encode("duende"));
            usuarioRepo.save(alumnoSinPapeles);

            // -- CREAR HORARIO DE PRUEBA --
            Horario horarioHoy = new Horario("2DAM", "VIERNES", LocalTime.MIN, LocalTime.MAX);
            horarioRepo.save(horarioHoy);

            System.out.println("✅ DATOS CARGADOS Y APP LISTA");
        };
    }
}