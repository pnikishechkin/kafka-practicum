# Kafka practicum

## Структура приложения
- контроллер
- продюсер
- консьюмер
- докер образ для настройки кластера Kafka

## Разворачивание приложения

1. Развернуть **`docker-compose_cp-kafka_3-nodes.yaml`** 
2. Создать через терминал топик  с 3 партициями и 2 репликами:
   - **`docker exec -it kafka-0 /bin/bash`**
   - **`cd /usr/bin`**
   - **`kafka-topics --create --topic my-topic --partitions 5 --replication-factor 1 --bootstrap-server localhost:9092`**
3. 

## Параметры docker файла для настройки кластера Kafka

### Параметры контроллера:

- **`KAFKA_ENABLE_KRAFT: "yes"`** - Включает новый KRaft режим (Kafka Raft metadata mode) - современную архитектуру без Zookeeper
- **`KAFKA_NODE_ID: 0`** - Уникальный идентификатор ноды в кластере, должен совпадать с ID в `QUORUM_VOTERS`
- **`KAFKA_PROCESS_ROLES: broker,controller`** - Роли процесса: **broker** (обработка данных) и **controller** (управление метаданными)
- **`KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER`** - Имя listener'а для контроллера (внутренняя коммуникация между нодами)

### Конфигурация кворума:

- **`KAFKA_CONTROLLER_QUORUM_VOTERS: 0@kafka-0:9093`** - Список участников кворума в формате: `<node_id>@<host>:<port>`. В нашем примере это single-node кластера, указывается только одна нода.
- **`KAFKA_KRAFT_CLUSTER_ID: "abcdefghijklmnopqrstuv"`** - Уникальный идентификатор кластера. **Критически важно** использовать фиксированное значение для перезапусков, иначе данные будут потеряны

### Настройки Listeners

- **`KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094`** - Определяет три типа listeners:
    - `PLAINTEXT://:9092` - внутренний listener для подключения клиентов внутри Docker-сети. **Кто использует:** Микросервисы, приложения в том же Docker network
    - `CONTROLLER://:9093` - внутренний listener для общения между контроллерами по протоколу Raft (KRaft). **Кто использует:** Только контроллеры Kafka
    - `EXTERNAL://:9094` - внешний listener для клиентов с хостовой машины. **Кто использует:** Разработчики, локальные приложения, CLI-утилиты
- **`KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-0:9092,EXTERNAL://localhost:9094`** - Адреса, которые Kafka сообщает клиентам для подключения:
    - `PLAINTEXT://kafka-0:9092` - для клиентов внутри Docker-сети (подключение по имени контейнера)
    - `EXTERNAL://localhost:9094` - для клиентов с хостовой машины (подключение через localhost)
- **`KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT`** - Сопоставляет listeners с протоколами безопасности. В данной конфигурации все listeners используют PLAINTEXT (без шифрования)

### Настройки репликации:

- **`KAFKA_DEFAULT_REPLICATION_FACTOR: 1`** - Фактор репликации по умолчанию для новых топиков. Задает количество копий данных для новых топиков.
- **`KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1`** - Фактор репликации для системного топика `__consumer_offsets`, то есть сколько копий данных системного топика на разных брокерах хранить. Данный топик нужен для хранений позиции потребителей (какое сообщение было прочитано последним) и отслеживания прогресса чтения для каждой группы потребителей.
- **`KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1`** - Фактор репликации для топиков транзакций - задает количество копий для топика транзакций `__transaction_state`. Данный топик: хранит состояние распределенных транзакций; координирует операции "все или ничего" между производителями; обеспечивает гарантию доставки сообщений.
- **`KAFKA_MIN_INSYNC_REPLICAS: 1`** - Минимальное количество in-sync реплик, необходимое для успешной записи. Гарантирует, что запись успешна только если данные записаны в N реплик.
- **`KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1`** - Минимальное количество in-sync реплик для топиков транзакций. То же, что `MIN_INSYNC_REPLICAS`, но специфично для топика транзакций.

### Настройки логирования:

- **`KAFKA_LOG_DIRS: "/kafka/logs"`** - Основная настройка директории логов
- **`KAFKA_LOG_RETENTION_HOURS: 168`** - Хранение логов 7 дней
- **`KAFKA_LOG_SEGMENT_BYTES: 1073741824`** - 1GB на сегмент
- **`KAFKA_LOG_RETENTION_CHECK_INTERVAL_MS`**: - Проверка каждые 5 минут