package com.example.tesouro.controller;

import com.example.tesouro.service.JogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jogo")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @GetMapping("/inicio")
    public ResponseEntity<?> inicio() {
        return ResponseEntity.ok(jogoService.iniciarJogo());
    }

    @GetMapping("/fase/1")
    public ResponseEntity<?> obterFase1(@RequestHeader("token") String token) {
        return jogoService.obterDesafio(1, token);
    }

    @PostMapping("/fase/1")
    public ResponseEntity<?> responderFase1(@RequestHeader("token") String token,
                                            @RequestBody String body) {
        return jogoService.validarResposta(1, token, body);
    }

    @GetMapping("/fase/42")
    public ResponseEntity<?> obterFase2(@RequestHeader("token") String token) {
        return jogoService.obterDesafio(2, token);
    }

    @PostMapping("/fase/42")
    public ResponseEntity<?> responderFase2(@RequestHeader("token") String token,
                                            @RequestBody String body) {
        return jogoService.validarResposta(2, token, body);
    }

    @GetMapping(value = "/CanalJudsonSantiago", params = "resposta")
    public ResponseEntity<?> responderFase3ViaGet(@RequestHeader("token") String token,
                                                  @RequestParam("resposta") String resposta) {
        return jogoService.validarRespostaViaGetFase3(token, resposta);
    }

    @GetMapping("/CanalJudsonSantiago")
    public ResponseEntity<?> obterFase3(@RequestHeader("token") String token) {
        return jogoService.obterDesafio(3, token);
    }

    @GetMapping("/LotusTech")
    public ResponseEntity<?> revelarTesouro(@RequestHeader("token") String token) {
        return jogoService.revelarTesouro(token);
    }
}
