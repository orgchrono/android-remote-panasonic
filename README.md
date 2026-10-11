# 📱 Panasonic DIGA Commander (Android IR)

Aplicativo Android nativo em **Kotlin** com interface moderna em **Jetpack Compose**, projetado especificamente para operar, diagnosticar e aplicar hacks de bancada em gravadores de DVD/HDD da linha **Panasonic DIGA** (ex: **DMR-EH55**, DMR-EH50, DMR-EH65, DMR-EX768, DMR-ES10/15) através do emissor infravermelho nativo (*Consumer IR Blaster*) do tablet **Samsung Galaxy Tab S 8.4 LTE (`SM-T805M`)**.

---

## ⚡ Como Testar Hoje Mesmo no Tablet (Sem Compilar Nada)

Se você já estiver com o tablet **Samsung SM-T805M** em mãos e quiser testar o hardware IR imediatamente contra o gravador:

1. Instale o aplicativo gratuito **[irplus - WAVE IR Remote](https://play.google.com/store/apps/details?id=net.binarymode.android.irplus)** na Google Play Store.
2. Transfira o arquivo para o tablet:
   ```
   Panasonic_DMR_EH55_Commander.irplus
   ```
3. No app **irplus**, toque no menu de 3 pontos no canto superior direito ➔ **Importar (Import)** ➔ selecione o arquivo `Panasonic_DMR_EH55_Commander.irplus`.
4. O layout completo com controle remoto e **12 botões dedicados de macro** já estará funcional:
   * **Horas Laser (`41`)**: Horímetro óptico (`L****`).
   * **Erro RAM Drive (`42`)**: Último erro da unidade óptica.
   * **Último Erro (`01`)**: Falha geral (`U**`, `H**`, `F**`).
   * **Limpar Erro (`98`)**: Grava `F00`.
   * **Ler Região (`02`)**: Região gravada (`04` Brasil, `00` Livre).
   * **Teste HDD (`31`)**: Varredura física de setores e MEIHDFS.
   * **Teste VFD (`00`)**: Todos os segmentos acesos.
   * **Ciclo Gaveta (`91`)**: Teste de fadiga mecânica.
   * **I/O Vídeo (`80`/`81`)**: Testes de barramento de saída.
   * **Sincronizar SET 1 / SET 2 / SET 3**: Pareamento de canal IR.
   * **Destravar Child Lock**: Pulso sustentado de ENTER.
   * **★ Hack Multi-Região ★**: 12 passos automáticos (DVD Region Free).
   * **⚠ Factory Reset (99)**: Inicialização total da NVRAM.

---

## 🧰 Catálogo Completo de Hacks e Funções Secretas

| Macro / Comando | Entrada | Descrição Técnica & Resposta no VFD |
| :--- | :--- | :--- |
| **Horas Laser** | `[4]` `[1]` | Exibe o tempo total acumulado em horas de emissão do cabeçote óptico (`L****`). |
| **Erro RAM Drive** | `[4]` `[2]` | Exibe o último código de falha da unidade de gravação óptica. |
| **Auto-Diagnóstico Geral** | `[0]` `[1]` | Exibe o código de erro geral mais recente gravado pelo CPU (`U61`, `H19`, etc.). |
| **Histórico EEPROM (Slots 1 a 5)** | `[6]` `[5]` ➔ `01`..`05` | Permite navegar pelos registros históricos dos últimos 19 erros salvos na EEPROM. |
| **Limpeza de Erro para F00** | `[9]` `[8]` | Grava `F00` na memória de falha temporária sem apagar configurações do usuário. |
| **Leitura de Código de Região** | `[0]` `[2]` | Exibe a região por 5 segundos (`04` Brasil, `01` EUA, `02` Europa, `00` Livre). |
| **Teste de Superfície HDD** | `[3]` `[1]` | Inspeciona os setores físicos do disco rígido e realoca blocos danificados no MEIHDFS. |
| **Teste de Display VFD** | `[0]` `[0]` | Acende 100% dos segmentos e ícones fluorescentes do painel frontal. |
| **Teste Mecânico de Gaveta** | `[9]` `[1]` | Inicia ciclo perpétuo de abertura e fechamento para teste de correia de carregamento. |
| **Validação de Saída de Vídeo** | `[8]` `[0]` / `[8]` `[1]` | Alterna padrões de teste para calibrar barramento RGB / Componente / S-Video. |
| **★ Hack Multi-Região ★** | `1`..`0` ➔ `AUDIO` ➔ `MEDIA` | Sequência de 12 passos com intervalo de 500 ms desbloqueando o aparelho para Região 0. |
| **Pareamento SET 1 / 2 / 3** | `OK` + `1`/`2`/`3` | Resolve os erros "SET 1", "SET 2" e "SET 3" quando o aparelho está em outro canal. |
| **Destravar Child Lock** | `[ENTER]` contínuo 5s | Desbloqueia os botões frontais travados em `LOCKED` ou `X-HOLD`. |
| **Bypass Noturno TVGOS** | `00000` no campo Zip Code | Desliga a busca noturna das 3:00 AM que desgasta o motor do HDD. |
| **Hard Reset de Fábrica** | `[9]` `[9]` | Limpa toda a EEPROM/NVRAM e restaura o aparelho para padrão de fábrica (`CLEAR`). |

## 📚 Documentação Técnica Completa (English Engineering Dossier)

Para o guia aprofundado de engenharia de bancada, consulte o documento:
👉 **[`PANASONIC_DIGA_HACKS_GUIDE.md`](PANASONIC_DIGA_HACKS_GUIDE.md)**

O dossiê inclui:
* **Física do Protocolo Kaseikyo 48-bit**: Portadora de 37.010 kHz, lead-in, codificação PWM de pulsos e estrutura de checksum XOR.
* **Tabela de 17 Hacks e Modos de Serviço**: Sequência de teclas, controle exigido, estado do aparelho, operação interna do microcontrolador SuperH SH-4 e efeito detalhado.
* **Tabela de Auto-Diagnóstico**: Decodificação completa de erros `U**` (operacionais), `H**` (hardware/servos) e `F**` (falhas críticas de EEPROM e barramento).
* **Referência Cruzada Hexadecimal**: Todos os 52 códigos de infravermelho no padrão Pronto Hex.

---

## 🎮 Espelhamento Integral do Controle Remoto Físico (EUR7729KG0)

O aplicativo e o perfil `.irplus` espelham com precisão cirúrgica todas as 52 funções do controle remoto original do Panasonic DMR-EH55:

| Seção / Módulo | Teclas & Comandos Espelhados |
| :--- | :--- |
| **Sistema & Seleção de Mídia** | `POWER`, `OPEN / CLOSE` (Eject da gaveta), `DRIVE SELECT` (Cicla HDD / DVD / SD-MMC), `INPUT SELECT` (L1, L2, L3, DV) |
| **Navegação & Menus** | `FUNCTIONS` (Menu DIGA principal), `DIRECT NAVIGATOR` (Lista de gravações), `TOP MENU` (Menu raiz de DVD), `OPTION / SUB MENU` (Menu de contexto), D-Pad (`▲`, `▼`, `◀`, `▶`), `ENTER / OK`, `RETURN`, `EXIT` |
| **Deck de Transporte** | `PLAY ▶`, `PAUSE ⏸`, `STOP ⏹`, `ANTERIOR |◀◀`, `PRÓXIMO ▶▶|`, `VOLTAR ◀◀`, `AVANÇAR ▶▶` |
| **Edição & Recursos DIGA** | `TIME SLIP` (Visualização simultânea / Picture-in-Picture), `MANUAL REPLAY` (-10s), `MANUAL SKIP` (+30s), `CREATE CHAPTER` (Criar capítulo), `DELETE TITLE` (Apagar título), `AUDIO`, `SUBTITLE` |
| **Gravação & Sintonizador** | `REC ●` (Gravação instantânea), `REC MODE` (XP, SP, LP, EP, FR), `EXT LINK` (Gravação via satélite/scart externo), `CH ▲`, `CH ▼`, `GUIDE` (Guia de programação), `PROG / CHECK` |
| **Teclado Numérico & Serviço** | Dígitos `1` a `9`, `0` (códigos nativos DIGA `0x10`..`0x19`), `MEDIA SERVER` (Acesso em rede), `CANCEL` |

---

## 🏗 Estrutura do Repositório

```
android-remote-panasonic/
├── .github/workflows/build-apk.yml        # CI/CD: Compilação automática de Release APK assinado
├── PANASONIC_DIGA_MULTI_MODEL_EXPANSION_PLAN.md # 🗺️ Pesquisa e plano multi-modelos (E, ES, EH, EX, Combos VHS)
├── PANASONIC_DIGA_HACKS_GUIDE.md          # 🇬🇧 Complete Engineering Dossier (17 Hacks, Service Codes, Error Decodes)
├── PANASONIC_DIGA_ANDROID_IR_PLAN.md      # Dossiê arquitetural e planejamento inicial
├── Panasonic_DMR_EH55_Commander.irplus    # Perfil XML completo (52 botões físicos + 12 macros prontas)
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
        │   │   │   └── PanasonicIrCodes.kt# Catálogo de códigos Pronto Hex & Macros expandidas
        │   │   └── ui/
        │   │       ├── DigaApp.kt         # Container de navegação e máquina de estados
        │   │       ├── RemoteTab.kt       # Aba 1: Controle remoto completo DIGA
        │   │       ├── ServiceTab.kt      # Aba 2: Diagnóstico de bancada, horímetro e slots EEPROM
        │   │       ├── HacksTab.kt        # Aba 3: Macro multi-região, SET 1/2/3 e TVGOS bypass
        │   │       ├── components/VfdDisplay.kt # Display virtual retro VFD
        │   │       └── theme/             # Cores, tipografia e tema
        │   └── res/                       # Strings, cores, estilos e temas XML
        └── test/java/com/vhs/digacommander/
            └── ProntoHexConverterTest.kt  # Testes unitários de pulsos e integridade de macros
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
  └──(Toque Cancelar)─────────┴───────────────────────────> [Cancelled]
```

1. **Mutex Serializado:** Impede colisão ou disparo acidental simultâneo de comandos.
2. **Janela Monotônica de 500 ms:** Previne estouro do buffer de interrupção do gravador.
3. **Cancelamento Reativo:** Permite abortar qualquer macro imediatamente.
4. **Modais de Confirmação:** O hack Multi-Região e o Hard Reset (99) exigem confirmação explícita antes do envio.

---

## 🚀 Como Abrir e Compilar no Android Studio

1. Abra o **Android Studio**.
2. Clique em **File ➔ Open...** e aponte para:
   ```
   c:\Users\danie\Documents\android-remote-panasonic
   ```
3. Aguarde o **Gradle Sync** concluir.
4. Conecte o tablet **Samsung SM-T805M** com a Depuração USB ativada (`adb devices`).
5. Clique em **Run 'app'** (`Shift + F10`) para instalar o APK diretamente no tablet.

---

## 🤖 CI/CD: Compilação Automática do APK (GitHub Actions)

O repositório está equipado com uma esteira de automação no GitHub Actions (`.github/workflows/build-apk.yml`):
* A cada commit ou pull request na branch `main`, o runner do GitHub executa os testes unitários (`./gradlew test`) e compila o APK de Release (`./gradlew assembleRelease`).
* O APK assinado é publicado como artefato de workflow (**`Panasonic-DIGA-Commander-release.apk`**) e pode ser baixado diretamente na aba **Actions** do repositório no GitHub sem necessidade de compilar no computador.
* Quando uma tag de versão for publicada (ex: `git tag v1.0.0 && git push origin v1.0.0`), uma **GitHub Release** oficial é gerada automaticamente com o APK anexado pronto para download.

