package greenlogsolution.greenlog.software;

import entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import repositories.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o usuário 'admin' já existe para não criar duplicatas
        if (usuarioRepository.findByLogin("admin").isEmpty()) {
            
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            
            // CRIPTOGRAFA a senha antes de salvar
            admin.setSenha(passwordEncoder.encode("senha123")); 
            
            usuarioRepository.save(admin);
            
            System.out.println(">>> Usuário 'admin' criado com a senha 'senha123' <<<");
        }
    }
}