# Autria — Front-end Android em Kotlin

Tela recriada em **Kotlin + Jetpack Compose** a partir da referência fornecida.

## Como abrir

1. Extraia o arquivo ZIP.
2. No Android Studio, escolha **Open** e selecione a pasta `AutriaCompose`.
3. Aguarde o Gradle sincronizar.
4. Execute em um emulador ou celular Android (API 24 ou superior).

Se você abriu uma versão anterior, feche o projeto, extraia este ZIP em uma
pasta nova e abra essa nova pasta. O projeto força Java e Kotlin para JVM 17.

## Onde editar

- Tela principal: `app/src/main/java/com/fiap/autria/AutriaScreen.kt`
- Estado exibido: `NavigationState`
- Activity: `app/src/main/java/com/fiap/autria/MainActivity.kt`

O conteúdo rola em telas menores, o controle de voz funciona e o botão de emergência responde ao toque prolongado.

## Integração com o FastAPI

- A tela consulta `GET /api/v1/app/state` a cada 500 ms.
- O switch envia `PATCH /api/v1/device/settings`.
- O botão de emergência envia `POST /api/v1/emergencies`.
- A URL fica em `app/build.gradle.kts`, no campo `API_BASE_URL`.
