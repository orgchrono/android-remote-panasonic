# 📱 Panasonic DIGA Commander (Android IR)

Aplicativo Android nativo em **Kotlin** com interface moderna em **Jetpack Compose**, projetado especificamente para operar, diagnosticar e aplicar hacks de bancada em gravadores de DVD/HDD da linha **Panasonic DIGA** (ex: **DMR-EH55**, DMR-EH50, DMR-EH65, DMR-EX768, DMR-ES10/15) através do emissor infravermelho nativo (*Consumer IR Blaster*) do tablet **Samsung Galaxy Tab S 8.4 LTE (`SM-T805M`)**.

---

## ⚡ Como Testar Hoje Mesmo no Tablet (Sem Compilar Nada)

Se você já estiver com o tablet **Samsung SM-T805M** em mãos e quiser testar o hardware IR imediatamente contra o gravador:

1. Instale o aplicativo gratuito **[irplus - WAVE IR Remote](https://play.google.com/store/apps/details?id=net.binarymode.android.irplus)** na Google Play Store.
2. Conecte o tablet ao computador via cabo USB ou transfira o arquivo:
   ```
   Panasonic_DMR_EH55_Commander.irplus
   ```
3. No app **irplus**, toque no menu de 3 pontos no canto superior direito ➔ **Importar (Import)** ➔ selecione o arquivo `Panasonic_DMR_EH55_Commander.irplus`.
4. O layout completo com botões de navegação, transporte, teclado numérico e os botões dedicados de macro (Horas Laser, Histórico de Falhas, Ler Região, Teste HDD e DVD Region Free) já estará 100% funcional.

---

## 🏗 Estrutura do Repositório

```
android-remote-panasonic/
├── PANASONIC_DIGA_ANDROID_IR_PLAN.md      # Especificação arquitetural de engenharia e códigos de serviço
├── Panasonic_DMR_EH55_Commander.irplus    # Perfil XML pronto para uso imediato no app irplus
├── DigaIrEngine.kt                        # Núcleo avulso do motor IR em Kotlin
├── README.md                              # Guia de instalação, compilação e uso
├── build.gradle.kts                       # Configuração de build raiz
├── settings.gradle.kts                    # Módulos do projeto
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle.kts                   # Configuração com Jetpack Compose & Kotlin
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml        # Permissão TRANSMIT_IR e hardware consumerir
        │   ├── java/com/vhs/digacommander/
        │   │   ├── MainActivity.kt        # Entry point do aplicativo Android
        │   │   ├── core/ir/
        │   │   │   ├── DigaIrEngine.kt    # ProntoHexConverter, ConsumerIrTransmitter, DigaMacroPipeline
        │   │   │   └── PanasonicIrCodes.kt# Catálogo de códigos Pronto Hex & Macros
        │   │   └── ui/
        │   │       ├── DigaApp.kt         # Container de navegação e máquina de estados
        │   │       ├── RemoteTab.kt       # Aba 1: Controle remoto completo DIGA
        │   │       ├── ServiceTab.kt      # Aba 2: Diagnóstico de bancada e horímetro
        │   │       ├── HacksTab.kt        # Aba 3: Macro multi-região e desbloqueios
        │   │       ├── components/VfdDisplay.kt # Display virtual retro VFD
        │   │       └── theme/             # Cores, tipografia e tema
        │   └── res/                       # Strings, cores, estilos e temas XML
        └── test/java/com/vhs/digacommander/
            └── ProntoHexConverterTest.kt  # Testes unitários da conversão matemática de pulsos
```

---

## 🔬 Validação Matemática do Protocolo Kaseikyo 48-bit

O conversor `ProntoHexConverter` transforma em tempo real strings hexadecimais no formato Philips Pronto Hex em vetores de microssegundos aceitos pelo `ConsumerIrManager.transmit()` do Android:

* **Frequência da Portadora:** $38.029\text{ Hz}$ (`006D`) ou $37.010\text{ Hz}$ (`0070`).
* **Lead-in (Cabeçalho):** Mark de $3.287\,\mu\text{s}$ / Space de $1.262\,\mu\text{s}$.
* **Bit Lógico `0`:** Mark de $421\,\mu\text{s}$ / Space de $421\,\mu\text{s}$ (proporção $1:1$).
* **Bit Lógico `1`:** Mark de $421\,\mu\text{s}$ / Space de $1.262\,\mu\text{s}$ (proporção $1:3$).
* **Comprimento:** Exatamente 100 pulsos alternados (50 pares de rajada = 48 bits de carga útil + enquadramento).

---

## 🛡 Pipeline Anti-Brick e Segurança

O gravador Panasonic DMR-EH55 utiliza um microprocessador Renesas SuperH SH-4 com janelas estritas de interrupção:

```
[Idle] ──(Disparo)──> [Running (Passo 1/12)] ──(500ms)──> [Running (Passo 2/12)] ──> [Completed]
  │                           │
  └──(Toque Abortar)──────────┴───────────────────────────> [Cancelled]
```

1. **Mutex Serializado:** Impede sobreposição ou disparo acidental concorrente de dois códigos IR ao mesmo tempo.
2. **Janela Monotônica de 500 ms:** Garante o processamento seguro de dígitos compostos sem estouro de buffer do gravador.
3. **Cancelamento Reativo:** Permite abortar a macro a qualquer momento durante a execução.
4. **Modais de Confirmação:** O hack Multi-Região e o Hard Reset de fábrica (99) exigem confirmação explícita antes do envio.

---

## 🚀 Como Abrir e Compilar no Android Studio

1. Abra o **Android Studio**.
2. Clique em **File ➔ Open...** e aponte para esta pasta:
   ```
   c:\Users\danie\Documents\android-remote-panasonic
   ```
3. Aguarde o **Gradle Sync** concluir o download dos plugins e dependências.
4. Conecte o tablet **Samsung SM-T805M** com a Depuração USB ativada (`adb devices`).
5. Clique em **Run 'app'** (`Shift + F10`) para instalar o APK diretamente no tablet.
