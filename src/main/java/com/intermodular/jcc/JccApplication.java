package com.intermodular.jcc;

import com.intermodular.jcc.config.RestConfig;
import com.intermodular.jcc.entities.Rol;
import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.DepartamentoRepository;
import com.intermodular.jcc.repository.HorarioRepository;
import com.intermodular.jcc.repository.UsuarioRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

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

        
        };
    }
}

