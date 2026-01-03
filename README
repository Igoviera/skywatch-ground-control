# 🛰️ SkyWatch – Ground Control Service
📌 Visão Geral

O SkyWatch Ground Control é o microserviço responsável por monitorar múltiplas aeronaves em tempo real, processando telemetria recebida via Kafka e tomando decisões automáticas ou manuais.

Ele atua como uma central de controle no solo.


## 🎯 Responsabilidades

- Consumir telemetria de aeronaves

- Manter o último estado conhecido da frota

- Gerar alertas operacionais

- Enviar comandos via Kafka

- Expor API REST para visualização (dashboard)

## 🧩 Arquitetura
Imgem

## 📡 Comunicação Kafka
📥 Consumer

- Topic: skywatch-telemetry

- Key: aircraftId

- Value: JSON (AircraftData)

📤 Producer

- Topic: skywatch-commands

- Key: aircraftId

- Value: String (ex: LAND)


## 🚨 Lógica de Monitoramento

- Combustível < 10%

  - Alerta crítico (emitido apenas uma vez)

- Combustível < 8%

  - Envio automático de comando de pouso

- Estado da frota mantido em memória (thread-safe)


## API REST

🔍 Status da Frota

```bash
  GET /api/status
```

🛬 Enviar Comando Manual

```bash
  POST /api/status/comando/{aircraftId}
```

Body:

```bash
 POUSAR
```

## 🖥️ Dashboard Web

- Interface HTML/JS

- Atualização periódica (polling)

- Destaque visual para combustível crítico

- Envio manual de comandos

## 🛠️ Tecnologias

- Java 17

- Spring Boot

- Spring Kafka

- Apache Kafka

- REST API

- HTML / JavaScript


## 🎯 Objetivo

- Este serviço demonstra conceitos reais de:

- Arquitetura orientada a eventos

- Monitoramento em tempo real

- Sistemas distribuídos

- Tomada de decisão automatizada
