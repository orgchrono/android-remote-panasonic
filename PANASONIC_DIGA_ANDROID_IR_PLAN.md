# 📱 Panasonic DIGA Commander — Especificação Arquitetural & Plano do Projeto Android IR

> **Status do Documento:** Engenharia Completa & Compilação de Hacks (Executado)  
> **Dispositivo Alvo:** Samsung Galaxy Tab S 8.4 LTE (`SM-T805M`) com emissor infravermelho integrado (*Consumer IR Blaster*)  
> **Aparelhos Compatíveis:** Panasonic DMR-EH55, DMR-EH50, DMR-EH65, DMR-EX768, DMR-ES10/15 e geração DIGA correlata  
> **Linguagem & Ambiente:** Kotlin (Android Nativo / Android Studio)

---

## 1. Dossiê Completo de Hacks, Códigos Secretos e Procedimentos de Bancada

Abaixo está o mapeamento exaustivo e verificado de todos os comandos e procedimentos documentados na literatura técnica e manuais de serviço Panasonic (*Panasonic Service Manual DMR-EH55EC / DMR-EH50*):

### 1.1 Entradas de Serviço e Diagnóstico (Service Mode)

O **Service Mode** do microprocessador Renesas SH-4 e do sub-controlador periférico da Panasonic é acessado de duas formas:
1. **Via Painel Frontal (Modo Físico):**
   Com o aparelho ligado e em **STOP** (sem reprodução, gaveta vazia):
   $$\mathbf{[STOP]} + \mathbf{[PLAY]} \quad \text{por 5 segundos}$$
   *(Em alguns modelos: aparelho em Standby, segurar $\mathbf{[STOP]} + \mathbf{[CH\ UP]}$ por 5 segundos)*.
2. **Via Infravermelho (Comando de Serviço Remoto):**
   Disparo da sequência de códigos de teste da Panasonic.

Uma vez no **Service Mode**, o display fluorescente frontal (VFD/FLD) exibe `SERVICE` e responde aos comandos numéricos do controle:

| Código Remoto | Nome da Função | O que faz / O que o display (VFD) exibe | Nível de Risco |
| :--- | :--- | :--- | :--- |
| **`4` `1`** | **Laser Used Time (Horímetro)** | Exibe o tempo total acumulado em horas de emissão do diodo laser óptico (ex: `L0185` = 185 horas). Crucial para avaliar a vida útil do cabeçote óptico. | **Zero** (Leitura) |
| **`4` `2`** | **RAM Drive Last Error** | Exibe o código do último erro específico registrado no drive óptico DVD-RAM. | **Zero** (Leitura) |
| **`0` `1`** | **Self-Diagnosis / Latest Error** | Exibe o código de erro geral mais recente (`U**`, `H**`, `F**`) gravado pelo microcontrolador. | **Zero** (Leitura) |
| **`6` `5` ➔ `01`..`19`** | **Error History (19 Slots EEPROM)** | Inicia a inspeção histórica dos últimos 19 erros armazenados na EEPROM (`Slot 1` = mais recente, até `Slot 19`). | **Zero** (Leitura) |
| **`0` `2`** | **Region Code Readout** | Exibe por 5 segundos no painel a região do DVD gravada na placa (`04` Brasil, `01` EUA, `02` Europa, `00` Multi-Região livre). | **Zero** (Leitura) |
| **`3` `1`** | **HDD Surface / Drive Integrity** | Dispara o teste interno de varredura de integridade física dos setores do disco rígido e realocação automática de setores no MEIHDFS. | **Baixo** (Não destrutivo) |
| **`0` `0`** | **VFD All-Segments Test** | Acende simultaneamente 100% dos segmentos e ícones do display frontal para diagnóstico de filamentos e trilhas do CI de display. | **Zero** (Visual) |
| **`8` `0` / `8` `1`** | **I/O & Video Output Test** | Força a alternância de padrões de teste para validação de barramento RGB/Componente/S-Video. | **Zero** (Diagnóstico) |
| **`9` `1`** | **Tray Cycle / Endurance Test** | Inicia ciclo contínuo de abertura e fechamento da gaveta para teste de desgaste mecânico de correia e engrenagens. | **Baixo** (Mecânico) |
| **`9` `8`** | **Clear Error Register (F00)** | Limpa o último código de erro pendente gravando `F00` na memória de diagnóstico sem apagar ajustes do usuário. | **Baixo** (Limpeza) |
| **`9` `9`** | **Factory Reset / NVRAM Clear** | Limpa toda a NVRAM/EEPROM, zera histórico de erros e restaura as configurações originais de fábrica (exibe `CLEAR`). | **Médio** (Limpa ajustes) |

*Para sair do Service Mode com segurança:* Transmitir o comando **`[POWER]`**.

---

### 1.2 Tabela de Interpretação Forense de Códigos de Erro (EEPROM)

Ao inspecionar o histórico com `[0] [1]` ou `[6] [5]`, o display reportará códigos das famílias **U**, **H** e **F**:

| Código de Erro | Causa Raiz / Subsistema | Ação Recomendada em Bancada |
| :--- | :--- | :--- |
| **`U61`** | Falha de comunicação no barramento entre o CPU Renesas SH-4 e o drive óptico DVD-RAM. | Inspecionar cabo flat, conector IDE/ATAPI e tensões de 5V/12V da fonte. |
| **`U80` / `U81`** | Falha de comunicação no barramento do HDD ou timeout de inicialização do disco rígido. | Verificar alimentação do HDD, setores MEIHDFS corrompidos ou capacitores da linha de 5V. |
| **`U88`** | Erro de leitura/gravação no cabeçote óptico ou disco com reflexividade degradada. | Checar horímetro com `41`, limpar lente óptica ou calibrar emissão. |
| **`U99`** | Hang-up geral do microprocessador principal com o timer/sub-CPU. | Resetar alimentação ou executar destravamento forçado via IR. |
| **`H19`** | Falha mecânica ou travamento do cooler de ventilação (*Fan Motor Error*). | Limpar ventoinha traseira, lubrificar rolamento ou substituir o cooler. |
| **`U59`** | Proteção térmica ativa (temperatura interna superior a 70 °C). | Substituir pasta térmica do processador SH-4 e desobstruir fluxo de ar. |
| **`F58`** | Erro crítico no hardware do controlador de drive. | Inspecionar CI controlador de motor spindle / sled. |

---

### 1.3 O Hack Multi-Região (DVD Region Free via IR Macro)

Os gravadores Panasonic da geração DIGA não possuem menu na tela para alteração de região. A troca para **Região 0 (Multi-Região)** é realizada através de uma injeção de sequência de pulsos no barramento IR com intervalo de tempo rigoroso de 500 ms.

#### A Sequência de Macro Homologada:
Com o aparelho **ligado** e a **gaveta do DVD aberta e vazia** (sem disco):

$$\mathbf{1} \xrightarrow{500\text{ms}} \mathbf{2} \xrightarrow{500\text{ms}} \mathbf{3} \xrightarrow{500\text{ms}} \mathbf{4} \xrightarrow{500\text{ms}} \mathbf{5} \xrightarrow{500\text{ms}} \mathbf{6} \xrightarrow{500\text{ms}} \mathbf{7} \xrightarrow{500\text{ms}} \mathbf{8} \xrightarrow{500\text{ms}} \mathbf{9} \xrightarrow{500\text{ms}} \mathbf{0} \xrightarrow{500\text{ms}} \mathbf{AUDIO} \xrightarrow{500\text{ms}} \mathbf{MEDIA\_SERVER}$$

* **Comportamento Esperado:** O gravador reinicia automaticamente ou o display exibe brevemente uma confirmação (como `1B` ou `00`).
* **Verificação Pós-Hack:** Entrar no Service Mode (`STOP` + `PLAY` por 5s) e apertar `0` `2`. O display deve acusar região `00` (Livre para todas as regiões).

---

### 1.4 Sincronização de Códigos de Controle (SET 1 / SET 2 / SET 3)

Quando múltiplos aparelhos Panasonic operam na mesma bancada/rack, o gravador pode ser comutado para os canais 1, 2 ou 3. Se houver divergência, o painel pisca **"SET 1"**, **"SET 2"** ou **"SET 3"** e rejeita todos os comandos:

* **Sincronizar Modo 1 (DVD 1):** Pressionar e segurar `OK` + `1` por 5 segundos.
* **Sincronizar Modo 2 (DVD 2):** Pressionar e segurar `OK` + `2` por 5 segundos (*resolve o erro "SET 2"*).
* **Sincronizar Modo 3 (DVD 3):** Pressionar e segurar `OK` + `3` por 5 segundos (*resolve o erro "SET 3"*).

---

### 1.5 Comandos de Emergência, Desbloqueio e Manutenção

* **Destravar "CHILD LOCK / X-HOLD / LOCKED":**
  * Ocorre quando a trava de painel é acionada e nenhum botão físico do gravador responde.
  * *Comando de Desbloqueio:* Segurar a tecla **`[ENTER]`** apontada para o aparelho por **5 segundos contínuos**.
* **Bypass Noturno TVGOS (Proteção contra Desgaste de HDD):**
  * O sistema *TV Guide On Screen* liga o HDD às 3:00 AM procurando sinais analógicos extintos, desgastando desnecessariamente o rolamento mecânico.
  * *Bypass:* Acessar *Functions ➔ Setup ➔ TV Guide Setup ➔ Zip Code* e injetar **`00000`** ou **`99999`**.
* **Ejeção Forçada de Gaveta Emperrada (Force Tray Eject):**
  * Desligar o aparelho; segurar simultaneamente **`[STOP]` + `[CH UP]`** no painel frontal por 5 segundos para pulso de sobrecorrente no motor de carregamento.

---

## 2. Engenharia do Protocolo Infravermelho Panasonic & Pronto Hex

### 2.1 Especificação Física do Protocolo Kaseikyo / Panasonic

Os gravadores Panasonic utilizam a especificação de codificação **Kaseikyo (Panasonic 48-bit)**:
* **Frequência da Portadora (*Carrier Frequency*):** $36.7\text{ kHz}$ a $38.0\text{ kHz}$ (frequência ideal: **$37.010\text{ kHz}$** ou **$38.029\text{ kHz}$**).
* **Pulso de Cabeçalho (*Header/Lead-in*):**
  * Mark (LED ligado): $\approx 3.287\,\mu\text{s}$ a $3.456\,\mu\text{s}$ ($128$ ciclos a $37\text{ kHz}$)
  * Space (LED desligado): $\approx 1.262\,\mu\text{s}$ a $1.728\,\mu\text{s}$ ($64$ ciclos a $37\text{ kHz}$)
* **Bit Lógico `0`:**
  * Mark: $\approx 421\,\mu\text{s}$ ($16$ ciclos)
  * Space: $\approx 421\,\mu\text{s}$ ($16$ ciclos — proporção $1:1$)
* **Bit Lógico `1`:**
  * Mark: $\approx 421\,\mu\text{s}$ ($16$ ciclos)
  * Space: $\approx 1.262\,\mu\text{s}$ ($48$ ciclos — proporção $1:3$)
* **Trailer / Stop Bit:** Mark de $421\,\mu\text{s}$ seguido de pausa de repouso ($\approx 74\text{ ms}$ a $100\text{ ms}$).

### 2.2 Conversão de Formato: Pronto Hex $\rightarrow$ `ConsumerIrManager` (Microssegundos)

O Android gerencia o hardware infravermelho nativo através de:
`android.hardware.ConsumerIrManager.transmit(carrierFrequency: Int, pattern: IntArray)`

O conversor [`ProntoHexConverter`](file:///c:/Users/danie/Documents/android-remote-panasonic/app/src/main/java/com/vhs/digacommander/core/ir/DigaIrEngine.kt#L17-L49) processa a string:
$$f = \frac{1.000.000}{\text{freq\_code} \times 0.241246}\text{ Hz}$$
$$\text{Duração em microssegundos} = \frac{\text{palavra\_hex} \times 1.000.000}{f}$$

---

## 3. O Hardware: Samsung Galaxy Tab S 8.4 LTE (`SM-T805M`)

* **Emissor IR Integrado:** *Consumer IR LED* montado na lateral do gabinete.
* **Driver de Hardware:** Suportado nativamente no Android 5.0+ Lollipop (API 21+) via `ConsumerIrManager`.
* **Manifest do Aplicativo:**
  ```xml
  <uses-feature android:name="android.hardware.consumerir" android:required="false" />
  <uses-permission android:name="android.permission.TRANSMIT_IR" />
  ```

---

## 4. Pipeline Anti-Brick com Mutex e Coroutines

```
[Idle] ──(Disparo)──> [Running (Passo 1/12)] ──(500ms)──> [Running (Passo 2/12)] ──> [Completed]
  │                           │
  └──(Toque Cancelar)─────────┴───────────────────────────> [Cancelled]
```

* **Fila Serializada com Mutex:** Impede que dois comandos colidam no barramento óptico.
* **Temporização Monotônica de 500 ms:** Previne estouro do buffer de interrupção do processador Renesas SH-4.
* **Cancelamento Instantâneo:** Permite abortar a macro a qualquer momento sem deixar o gravador em estado instável.
* **Confirmação Dupla:** Modais com avisos explícitos para comandos destrutivos (como *Hard Reset 99*).
