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
import java.time.LocalTime;
import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(RestConfig.class)
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
            System.out.println("ACTUALIZANDO DATOS");

            usuarioRepo.deleteAll();
            deptRepo.deleteAll();
            horarioRepo.deleteAll();

            // -- DEPARTAMENTOS --
            Departamento depInfo = new Departamento("Informática");
            Departamento depLengua = new Departamento("Lengua");
            deptRepo.saveAll(Arrays.asList(depInfo, depLengua));

            // -- USUARIOS (Ahora con DNI, Gmail, Fecha Nacimiento y Baja) --
            // 1. PROFESOR
            Usuario profe = new Usuario();
            profe.setNombre("Profesor");
            profe.setApellidos("Xavier");
            profe.setDni("11111111A");
            profe.setGmail("profe@instituto.com");
            profe.setFechaNacimiento(LocalDate.of(1980, 1, 1));
            profe.setRol(Rol.PROFESOR);
            profe.setNfcToken("PROFE1");
            profe.setBaja(false); // No esta de baja
            profe.setDepartamento(depInfo);
            profe.setPassword(encoder.encode("1234"));
            usuarioRepo.save(profe);

            // 2. ALUMNO
            Usuario alumno = new Usuario();
            alumno.setNombre("Peter");
            alumno.setApellidos("Parker");
            alumno.setDni("22222222B");
            alumno.setGmail("spiderman@gmail.com");
            alumno.setFechaNacimiento(LocalDate.of(2005, 8, 10));
            alumno.setRol(Rol.ALUMNO);
            alumno.setNfcToken("ALUMNO1");
            alumno.setCurso("2DAM");
            alumno.setBaja(false);
            alumno.setPassword(encoder.encode("spiderman"));
            usuarioRepo.save(alumno);

            // -- HORARIO --
            Horario horarioHoy = new Horario("2DAM", "VIERNES", LocalTime.MIN, LocalTime.MAX);
            horarioRepo.save(horarioHoy);

            System.out.println("✅ DATOS CARGADOS.");
            System.out.println("Login Profe: User '11111111A' / Pass '1234'");
            System.out.println("Login Alumno: User '22222222B' / Pass 'spiderman'");
        };
    }
}
