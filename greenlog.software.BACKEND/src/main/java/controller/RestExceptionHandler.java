package controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
	
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Captura exceções de validação de regras de negócio (ex: caminhão não pode coletar um resíduo).
     * @param ex A exceção capturada.
     * @return Uma resposta HTTP 400 (Bad Request) com a mensagem de erro.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> body = Map.of("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Captura exceções quando uma entidade (ex: Rota) não é encontrada no banco.
     * @param ex A exceção capturada.
     * @return Uma resposta HTTP 404 (Not Found) com a mensagem de erro.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> body = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Captura exceções de violação de integridade do banco de dados.
     * Isso acontece quando tentamos inserir dados que violam uma restrição única,
     * como criar um itinerário para um caminhão em uma data já agendada.
     *
     * @param ex A exceção do banco de dados.
     * @return Uma resposta HTTP 409 (Conflict) com uma mensagem clara.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensagem = "Erro de integridade dos dados. A operação viola uma restrição do banco de dados.";
        
        // Tenta encontrar a causa raiz e verifica se é a nossa restrição específica
        Throwable causaRaiz = ex.getRootCause();
        if (causaRaiz != null && causaRaiz.getMessage().contains("uk_itinerario_caminhao_data")) {
            mensagem = "Erro: Já existe um itinerário para este caminhão nesta data.";
        }
        
        Map<String, String> body = Map.of("error", mensagem);
        // HttpStatus.CONFLICT (409) é o código ideal para este tipo de erro.
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // Pega a mensagem de erro que você definiu na anotação (ex: "A placa é obrigatória.")
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(", ")); // Junta vários erros com vírgula, se houver

        Map<String, String> body = Map.of("error", errorMessage);
        
        // Retorna um erro 400 (Bad Request), que é o correto para dados de entrada inválidos.
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
