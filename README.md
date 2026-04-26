# ⏳ JikanHub (時間) — O seu tempo, organizado.

JikanHub é uma aplicação moderna de gerenciamento de tarefas (To-Do List) focada em produtividade e experiência do usuário. O projeto utiliza uma arquitetura **Local-First**, garantindo que você possa gerenciar suas tarefas offline e sincronizá-las automaticamente com a nuvem quando estiver online.

![Status do Projeto](https://img.shields.io/badge/Status-Pronto_para_Play_Store-green?style=for-the-badge)
![Tech Stack](https://img.shields.io/badge/Stack-Kotlin_%7C_Compose_%7C_Ktor-blue?style=for-the-badge)

---

## ✨ Funcionalidades Principais

- 📱 **Interface Premium:** Design inspirado na estética japonesa (Jikan - Tempo), com suporte nativo a Modo Escuro e animações fluidas.
- 🔄 **Sincronização Bidirecional:** Seus dados são salvos localmente (Room) e sincronizados via WorkManager com o servidor.
- 🤖 **Sugestões com IA:** Use inteligência artificial para gerar sub-tarefas automaticamente com base no título e descrição da tarefa principal.
- 🔔 **Sistema de Lembretes:** Notificações inteligentes com múltiplos intervalos (no horário, 15 min antes, 12h antes, etc).
- 🔐 **Segurança & Multi-usuário:** Isolamento total de dados por conta, autenticação JWT, e suporte a Login com Google.
- 📊 **Widgets:** Acompanhe suas tarefas do dia diretamente na tela inicial do Android.
- 📖 **Tutorial Interativo:** Sistema de onboarding para guiar novos usuários nas funcionalidades principais.

---

## 🛠️ Stack Tecnológica

### Android App
- **Linguagem:** Kotlin
- **UI:** Jetpack Compose
- **Banco Local:** Room DB
- **Injeção de Dependência:** Hilt
- **Rede:** Retrofit + OkHttp
- **Processamento em Background:** WorkManager
- **Segurança:** ProGuard/R8, Encrypted DataStorage (Planejado).

### Backend (API)
- **Linguagem:** Kotlin
- **Framework:** Ktor
- **Banco de Dados:** PostgreSQL
- **ORM:** Exposed
- **Autenticação:** JWT (JSON Web Tokens)
- **Container:** Docker & Docker Compose

---

## 🚀 Como Executar o Projeto

### 1. Configurando o Backend
O backend está pronto para ser rodado via Docker.

1.  Navegue até a raiz do projeto.
2.  Crie um arquivo `.env` baseado no `.env.example` (não incluído no git por segurança).
3.  Execute o comando:
    ```bash
    docker compose up -d --build
    ```
A API estará disponível em `http://seu-ip:8080` (ou HTTPS se configurado).

### 2. Configurando o App Android
1.  Abra a pasta `/android` no Android Studio.
2.  Certifique-se de que o `BASE_URL` no `build.gradle.kts` aponta para o seu servidor.
3.  Sincronize o Gradle e rode o app em um emulador ou dispositivo físico.

---

## 🛡️ Segurança & Play Store

Este projeto foi auditado e configurado para cumprir as diretrizes da Google Play:
- **Tráfego Seguro:** Configurado para exigir HTTPS em produção.
- **Ofuscação:** Regras de ProGuard integradas para proteger o código-fonte.
- **Privacidade:** Configurações de backup desativadas para dados sensíveis.
- **Validação:** Sistema rigoroso de validação de inputs (E-mail, Senha, Nome).

---

## 📄 Licença

Este projeto é para fins educacionais e uso pessoal. Verifique a licença dos assets antes de qualquer distribuição comercial.

---
*Desenvolvido com ❤️ para dominar o tempo.*
