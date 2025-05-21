package com.example.tesouro.service;

import com.example.tesouro.model.Fase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JogoService {

    // Armazena o progresso dos jogadores, identificados por token único
    private final Map<String, Integer> progresso = new ConcurrentHashMap<>();

    // Define as fases do jogo
    private final Map<Integer, Fase> fases = new HashMap<>();

    // Palavra secreta final, revelada apenas ao concluir o jogo
    private final String palavraSecreta = "LOTUS";

    public JogoService() {
        // Criação das fases com explicações didáticas para cada etapa
        fases.put(1, new Fase(
                1,
                """
                👋 Bem-vindo(a) à Fase 1! Esta fase é um teste simples para aprender como interagir com a API. ✅ O que fazer: - Envie uma requisição **POST** para este mesmo endpoint. - O corpo da requisição deve conter um JSON com a chave `resposta`. 📝 Exemplo: { "resposta": "o que é a vida?" }""",
                "42",
                "Dica: Qual é a resposta para a vida, o universo e tudo mais? Caso tiver dúvida pergunte ao professor Leonardo Casillo"
        ));

        fases.put(2, new Fase(
                2,
                """
                📺 Fase 2 - Conhecimentos da comunidade UFERSA! ✅ O que fazer: - Envie novamente um **POST** com o JSON no mesmo formato da fase anterior. - A resposta correta é o nome de um canal do YouTube conhecido por ensinar programação de forma didática. 📝 Exemplo: { "resposta": "Nome do Canal" }""",
                "Judson Santiago",
                "Dica: Nome do professor mais didático de programação de computadores."
        ));

        fases.put(3, new Fase(
                3,
                """
                🏢 Fase 3 - Caça ao nome no Instagram! ✅ O que fazer: - Envie uma **requisição GET** para este mesmo endpoint - Coloque a resposta como parâmetro de query string. 📝 Exemplo: /jogo/CanalJudsonSantiago?resposta=nome_instagram""",
                "lotustecno",
                "Dica: Nome de usuário no Instagram da melhor empresa júnior do LCC."
        ));
    }

    // Caminhos simbólicos (sem efeito prático, apenas para guiar o jogador)
    private final Map<Integer, String> caminhos = Map.of(
            1, "/jogo/fase/42",
            2, "/jogo/CanalJudsonSantiago",
            3, "/jogo/LotusTech"
    );

    /**
     * Inicia o jogo e gera um token para acompanhar o progresso do jogador.
     * A resposta contém:
     * - Um token único
     * - Instruções de como acessar a primeira fase
     */
    public Map<String, String> iniciarJogo() {
        String token = UUID.randomUUID().toString();
        progresso.put(token, 1); // Inicia na fase 1

        return Map.of(
                "mensagem", """
            🎮 Jogo iniciado! 🧠 Como funciona? Você passará por 3 fases resolvendo enigmas. A cada fase correta, você desbloqueia a próxima. 🔑 IMPORTANTE: O que é HEADER? Em chamadas HTTP, o *header* é onde você pode enviar informações adicionais, como autenticação. Neste jogo, você precisa enviar seu `token` em **todas** as requisições após iniciar o jogo. ✅ Como enviar: Envie um header com: - Key: token - Value: [seu_token] 🧭 Próximo passo: Envie uma requisição **GET** para: `/jogo/fase/1` Inclua o token como header para acessar a primeira fase.""",
                "token", token,
                "proximaEtapa", "/jogo/fase/1"
        );
    }

    /**
     * Retorna o desafio da fase atual
     */
    public ResponseEntity<?> obterDesafio(int numero, String token) {
        if (!progresso.containsKey(token) || progresso.get(token) < numero) {
            return ResponseEntity.status(403).body(Map.of(
                    "erro", "⛔ Você ainda não desbloqueou essa fase. Complete a anterior primeiro!"
            ));
        }

        Fase fase = fases.get(numero);
        return ResponseEntity.ok(Map.of(
                "fase", String.valueOf(fase.getNumero()),
                "descricao", fase.getDesafio(),
                "dica", fase.getDica()
        ));
    }

    /**
     * Valida a resposta enviada via POST (usada nas fases 1 e 2)
     */
    public ResponseEntity<?> validarResposta(int numero, String token, String respostaJson) {
        if (!progresso.containsKey(token) || progresso.get(token) != numero) {
            return ResponseEntity.status(403).body(Map.of("erro", "⛔ Fase inválida ou fora de ordem."));
        }

        String resposta;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> dados = mapper.readValue(respostaJson, new TypeReference<>() {});
            resposta = dados.getOrDefault("resposta", "");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                    "erro", "❌ JSON mal formatado. Use o modelo: { \"resposta\": \"sua_resposta\" }"
            ));
        }

        Fase fase = fases.get(numero);
        if (resposta.equalsIgnoreCase(fase.getResposta())) {
            progresso.put(token, numero + 1);
            return ResponseEntity.ok(Map.of(
                    "mensagem", "✅ Resposta correta! Parabéns.",
                    "Proxima Fase", caminhos.getOrDefault(numero, "/jogo/LotusTech")
            ));
        } else {
            return ResponseEntity.status(400).body(Map.of("erro", "❌ Resposta incorreta. Tente novamente."));
        }
    }

    /**
     * Valida a resposta da fase 3 (enviada via GET com query string)
     */
    public ResponseEntity<?> validarRespostaViaGetFase3(String token, String resposta) {
        int numero = 3;

        if (!progresso.containsKey(token) || progresso.get(token) != numero) {
            return ResponseEntity.status(403).body(Map.of("erro", "⛔ Fase inválida ou fora de ordem."));
        }

        Fase fase = fases.get(numero);
        if (resposta.equalsIgnoreCase(fase.getResposta())) {
            progresso.put(token, numero + 1);
            return ResponseEntity.ok(Map.of(
                    "mensagem", "✅ Correto!",
                    "Proxima Fase", "/jogo/LotusTech"
            ));
        } else {
            return ResponseEntity.status(400).body(Map.of("erro", "❌ Resposta incorreta. Tente novamente."));
        }
    }

    /**
     * Entrega o prêmio final ao jogador que completou todas as fases
     */
    public ResponseEntity<?> revelarTesouro(String token) {
        if (progresso.getOrDefault(token, 0) >= fases.size() + 1) {
            return ResponseEntity.ok(Map.of(
                    "mensagem", "🎉 Parabéns! Você encontrou o tesouro escondido!",
                    "palavraSecreta", palavraSecreta
            ));
        }
        return ResponseEntity.status(403).body(Map.of("erro", "🔒 Você ainda não completou todas as fases."));
    }
}
