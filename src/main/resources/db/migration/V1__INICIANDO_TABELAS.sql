CREATE SEQUENCE IF NOT EXISTS ambientes_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS reservas_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE ambientes
(
    id        BIGINT NOT NULL,
    tipo      VARCHAR(255),
    descricao VARCHAR(255),
    CONSTRAINT pk_ambientes PRIMARY KEY (id)
);

CREATE TABLE reservas
(
    id                    BIGINT NOT NULL,
    data_de_reserva       date,
    data_reservada_inicio TIMESTAMP WITHOUT TIME ZONE,
    data_reservada_fim    TIMESTAMP WITHOUT TIME ZONE,
    descricao_reserva     VARCHAR(255),
    ambiente_id           BIGINT NOT NULL,
    CONSTRAINT pk_reservas PRIMARY KEY (id)
);

ALTER TABLE reservas
    ADD CONSTRAINT FK_RESERVAS_ON_AMBIENTE FOREIGN KEY (ambiente_id) REFERENCES ambientes (id);