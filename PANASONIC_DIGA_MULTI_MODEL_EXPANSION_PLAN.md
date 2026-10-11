# 🗺️ Panasonic DIGA Multi-Model Expansion Plan & Technical Dossier

Este documento estabelece a pesquisa técnica aprofundada, a engenharia de sinais e o plano arquitetural para expandir o **Panasonic DIGA Commander** para suportar dezenas de modelos de gravadores Panasonic DIGA (linhas **DMR-E**, **DMR-ES**, **DMR-EH**, **DMR-EX**, **DMR-EZ**, **combos VHS/DVD** e **Blu-ray DMR-BW**).

---

## 1. Fundamentos Matemáticos do Protocolo Kaseikyo 48-bit

Todos os gravadores e reprodutores Panasonic fabricados entre 1998 e 2018 utilizam a variante Panasonic do protocolo japonês **Kaseikyo 48-bit** (frequência de portadora de **37.010 kHz**).

A notação universal IRP (Infra-Red Protocol) do sinal Panasonic é:
$$\{37\text{k}, 432\}<1, -1, 1, -3>(8, -4, 2:8, 32:8, D:8, S:8, F:8, (D \oplus S \oplus F):8, 1, -173)+$$

### 1.1 Anatomia do Frame de 48 Bits
Cada transmissão é composta por 6 bytes de dados transmitidos LSB (Least Significant Bit first), totalizando 24 pares de rajada Mark/Space (48 pulsos de dados) + 1 par de lead-in + 1 pulso final de encerramento = **100 pulsos alternados**:

| Posição | Campo | Tamanho | Valor Fixo / Variável | Descrição Técnica |
| :--- | :--- | :--- | :--- | :--- |
| **Byte 1** | Fabricante Low | 8 bits | `0x02` (decimal 2) | Identificador Matsushita / Panasonic |
| **Byte 2** | Fabricante High | 8 bits | `0x20` (decimal 32) | Identificador Kaseikyo Panasonic |
| **Byte 3** | **Device ($D$)** | 8 bits | `0xB0` (176) ou `0x90` (144) | Endereço do dispositivo primário |
| **Byte 4** | **Subdevice ($S$)** | 8 bits | `0x00`, `0x10` ou `0x20` | Canal de pareamento (SET 1, 2 ou 3) |
| **Byte 5** | **Command ($F$)** | 8 bits | `0x00` a `0xFF` | Código Original do Botão (OBC) |
| **Byte 6** | **Checksum** | 8 bits | $D \oplus S \oplus F$ | Paridade XOR de integridade estrita |

---

## 2. O Mistério Resolvido dos Modos de Controle: SET 1, SET 2 e SET 3

Uma das maiores dores de cabeça para proprietários e técnicos de bancada de gravadores Panasonic é o gravador travar na mensagem **"SET 1"**, **"SET 2"** ou **"SET 3"** no painel frontal VFD, ignorando completamente o controle remoto.

A pesquisa de engenharia revela o mecanismo exato:
O gravador Panasonic DMR altera o seu registrador de **Subdevice ($S$)** quando o usuário altera o canal de controle no menu de Setup para evitar que dois gravadores no mesmo rack respondam juntos.

| Modo Exibido no VFD | Subdevice em Hex ($S$) | Subdevice Decimal | Combinação no Controle Físico |
| :--- | :--- | :--- | :--- |
| **SET 1** (Padrão de Fábrica) | `0x00` | 0 | Segurar `ENTER / OK` + `1` por 5 segundos |
| **SET 2** | `0x10` | 16 | Segurar `ENTER / OK` + `2` por 5 segundos |
| **SET 3** | `0x20` | 32 | Segurar `ENTER / OK` + `3` por 5 segundos |

### 💡 Impacto Arquitetural no Aplicativo:
Em vez de hardcodar apenas o Subdevice `0x00`, o nosso motor em Kotlin pode calcular os pulsos dinamicamente. Um seletor de 3 estados na barra superior do app:
`[ CANAL 1 (SET 1) ] | [ CANAL 2 (SET 2) ] | [ CANAL 3 (SET 3) ]`
permitirá que o técnico altere instantaneamente a transmissão de qualquer um dos 52 botões para o canal correspondente, destravar gravadores presos em "SET 2/3" sem precisar do controle original!

---

## 3. Matriz de Endereçamento de Dispositivos ($D$)

| Tipo de Deck / Aparelho | Código Device ($D$) | Subdevice Típico ($S$) | Exemplos de Modelos |
| :--- | :--- | :--- | :--- |
| **Deck DVD / HDD (Padrão DIGA)** | `0xB0` (176) | `0x00` / `0x10` / `0x20` | DMR-E50, E55, E85H, ES10, ES15, EH50, EH55, EH65, EX77, EZ48V |
| **Deck VCR (Combos VHS/DVD)** | `0x90` (144) | `0x00` / `0x10` / `0x20` | DMR-ES30V, ES35V, EH75V, EZ47V, EZ48V, NV-VP30, NV-VP31, NV-VP33 |
| **Controle de TV Panasonic** | `0x01` (1) ou `0x08` (8) | `0x00` | Botões `TV POWER`, `TV VOL +/-`, `TV AV` no topo do controle |
| **Blu-ray DIGA (Extensão)** | `0xB0` (176) | `0x00` (OBCs estendidos) | DMR-BW500, DMR-BWT700, DMR-BZT710 |

---

## 4. Taxonomia das Famílias Panasonic DIGA (2001 – 2012)

### Família 1: DMR-E Series (Gerações 1 e 2 — 2001 a 2004)
* **Modelos Representativos:**
  * *DVD-RAM / DVD-R:* DMR-E20, DMR-E30, DMR-E50, DMR-E55, DMR-E60, DMR-E65.
  * *DVD + HDD:* DMR-E80H (40GB), DMR-E85H (80GB), DMR-E95H (160GB), DMR-E100H (120GB).
* **Particularidades:**
  * Suporte a DVD-RAM e DVD-R (não aceitavam mídias +R/+RW na época).
  * Teclas dedicadas: `TIME SLIP` (Chasing Playback em DVD-RAM), `F-REC` (Flexible Recording), `CM SKIP`.
  * **Hacks de Serviço:** Mesmos códigos de serviço (`41` horas laser, `01` erros, `02` região, `99` clear).
  * Ponto de atenção de bancada: Capacitor `C1601` na fonte do E85H que causa o loop "PLEASE WAIT".

### Família 2: DMR-ES Series (Os Campeões de Pass-Through / TBC — 2005 a 2006)
* **Modelos Representativos:**
  * DMR-ES10, DMR-ES15, DMR-ES20, DMR-ES25.
* **Importância Crítica no Mercado de Preservação VHS:**
  * O **DMR-ES10** e o **DMR-ES15** são internacionalmente aclamados por arquivistas e laboratórios de digitalização VHS (comunidades *DigitalFAQ* e *VideoHelp*).
  * Eles incorporam o chip proprietário **Matsushita MN673794 (DVP)**, que executa **Line TBC (Time Base Corrector) de quadro inteiro** e sincronizador de jitter agressivo na entrada analógica (LINE 1 / LINE 2).
  * Muitos usuários usam esses aparelhos exclusivamente em modo *passthrough* (Entrada VHS ➔ ES10/15 ➔ Saída Component/S-Video para placa de captura), sem sequer gravar em DVD.
* **Comandos Chave:**
  * `INPUT SELECT` (para ciclar Line 1, Line 2, DV).
  * `STATUS` e `DISPLAY` (para checar formato do sinal).
  * `FUNCTIONS` (para configurar redução de ruído de entrada - Line-in NR).

### Família 3: DMR-EH Series (Carros-Chefe HDD + DVD — 2005 a 2008)
* **Modelos Representativos:**
  * DMR-EH50, DMR-EH52, DMR-EH55, DMR-EH60, DMR-EH65, DMR-EH67, DMR-EH68, DMR-EH69.
* **Particularidades:**
  * Hard Disks de 80 GB a 250 GB formatados no sistema proprietário **MEIHDFS**.
  * Slot para cartão SD (reprodução de fotos JPEG / transferência).
  * Saída de vídeo HDMI 1080p (nos modelos EH65, EH67 em diante).
  * Possui o conjunto mais completo de comandos de edição: `CREATE CHAPTER`, `DELETE TITLE`, `MANUAL SKIP (+30s)`, `MANUAL REPLAY (-10s)`.
  * Suporta o famoso **Hack Multi-Região de 12 passos** e o **Bypass do TVGOS (00000)**.

### Família 4: DMR-EX e DMR-EZ Series (Sintonizadores Digitais DVB-T / ATSC — 2006 a 2010)
* **Modelos Representativos:**
  * *Europa (DVB-T Freeview):* DMR-EX75, DMR-EX77, DMR-EX85, DMR-EX87, DMR-EX768, DMR-EX79, DMR-EX89.
  * *EUA / Américas (ATSC / Clear QAM):* DMR-EZ27, DMR-EZ28, DMR-EZ37V, DMR-EZ47V, DMR-EZ48V, DMR-EZ49V.
* **Comandos Adicionais:**
  * `GUIDE` (Guia eletrônico de programação digital de 7 dias).
  * `DIGITAL / ANALOG` (Alterna entre sintonizadores analógico e digital).
  * `HDMI RESOLUTION` (Alterna 576p, 720p, 1080i, 1080p).
  * `VIERA Link` (Controle conjunto via barramento HDMI CEC).

### Família 5: Combos VHS + DVD / Dual-Deck (Estações de Cópia Direta)
* **Modelos Representativos:**
  * NV-VP30, NV-VP31, NV-VP32, NV-VP33 (reprodutores combo VCR/DVD).
  * DMR-E75V, DMR-ES30V, DMR-ES35V (gravadores DVD + VCR Hi-Fi).
  * DMR-EH75V (Super combo: HDD 80GB + DVD + VCR Hi-Fi).
  * DMR-EZ47V, DMR-EZ48V (Combo DVB/ATSC + HDMI + DVD + VCR).
* **Necessidade Técnica de Endereçamento Duplo:**
  * O aparelho possui dois receptores lógicos no mesmo chassi:
    * Comandos de DVD respondem ao Device `0xB0`.
    * Comandos do VCR respondem ao Device `0x90`.
  * **Comandos Especiais do Deck VCR:**
    * `VCR PLAY`, `VCR STOP`, `VCR PAUSE`, `VCR REW`, `VCR FF`, `VCR REC`.
    * `TRACKING +` e `TRACKING -` (Ajuste fino de cabeçote do VHS).
    * `ONE-TOUCH DUBBING: VHS ➔ DVD` (Cópia direta de fita para disco).
    * `ONE-TOUCH DUBBING: DVD ➔ VHS` (Cópia de disco para fita).
    * `DRIVE SELECT: VHS` (Alterna o foco de exibição para a fita).

---

## 5. Arquitetura da Expansão no Código (Frente 2)

### 5.1 Gerador Paramétrico de Pulsos (`KaseikyoPulseGenerator.kt`)
Em vez de depender exclusivamente de strings estáticas pré-compiladas, implementaremos um gerador universal:

```kotlin
object KaseikyoPulseGenerator {
    private const val CARRIER_FREQ = 37010
    private const val LEAD_IN_MARK = 3459
    private const val LEAD_IN_SPACE = 1729
    private const val BIT_MARK = 432
    private const val BIT_0_SPACE = 432
    private const val BIT_1_SPACE = 1297

    fun generateProntoHex(device: Int, subdevice: Int, command: Int): String {
        val oem1 = 0x02
        val oem2 = 0x20
        val checksum = device xor subdevice xor command
        val bytes = intArrayOf(oem1, oem2, device, subdevice, command, checksum)
        // Converte os 6 bytes (48 bits LSB) em palavras Pronto Hex 0000 0070 ...
        ...
    }
}
```

### 5.2 Modelo de Perfil de Equipamento (`DigaModelProfile.kt`)
```kotlin
enum class DigaFamily {
    SERIES_E,       // E50, E55, E85H
    SERIES_ES,      // ES10, ES15 (TBC Pass-through)
    SERIES_EH,      // EH50, EH55, EH65, EH67 (HDD+DVD)
    SERIES_EX_EZ,   // EX77, EZ48V (Digital Tuner + HDMI)
    COMBO_VHS_DVD   // ES30V, EH75V, EZ48V (VCR Device 0x90 + Dubbing)
}

data class DigaModelProfile(
    val id: String,
    val displayName: String,
    val family: DigaFamily,
    val hasHdd: Boolean,
    val hasVcr: Boolean,
    val hasHdmi: Boolean,
    val defaultSubdeviceMode: Int = 0x00 // 0x00 (SET 1), 0x10 (SET 2), 0x20 (SET 3)
)
```

### 5.3 Biblioteca de Perfis XML `.irplus`
Geraremos no repositório perfis XML dedicados para cada grande família, prontos para importação no `irplus` e no `IR Blaster (iodn)`:
1. `Panasonic_DMR_EH55_Commander.irplus` *(Já existente e bit-perfect)*
2. `Panasonic_DIGA_ES10_ES15_TBC_Passthrough.irplus` *(Otimizado para bancada de captura VHS)*
3. `Panasonic_DIGA_Combo_VHS_DVD_EH75V_ES30V.irplus` *(Com controle dual VCR 0x90 + Tracking + Dubbing)*
4. `Panasonic_DIGA_Universal_Remote_Modes_1_2_3.irplus` *(Com macros para alternar instantaneamente entre SET 1, 2 e 3)*

---

## 6. Próximos Passos de Implementação
1. Implementar o `KaseikyoPulseGenerator.kt` no pacote `core/ir`.
2. Adicionar o seletor visual de canal (`SET 1 / 2 / 3`) na interface do `RemoteTab.kt`.
3. Criar os perfis XML adicionais no diretório raiz do projeto.
4. Integrar o workflow do GitHub Actions para compilação automática do APK Release.
