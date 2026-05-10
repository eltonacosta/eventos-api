INSERT INTO ambientes (id, tipo, descricao) VALUES
    (1, 'Sala de reuniao', 'Sala 01 - Reunioes pequenas'),
    (2, 'Auditorio', 'Auditorio principal'),
    (3, 'Laboratorio', 'Laboratorio de informatica'),
    (4, 'Sala de aula', 'Sala 204'),
    (5, 'Coworking', 'Espaco compartilhado');

INSERT INTO reservas (
    id,
    data_de_reserva,
    data_reservada_inicio,
    data_reservada_fim,
    descricao_reserva,
    ambiente_id
) VALUES
    (
        1,
        DATE '2026-05-03',
        TIMESTAMP '2026-05-03 09:00:00',
        TIMESTAMP '2026-05-03 12:00:00',
        'Reuniao de planejamento',
        1
    ),
    (
        2,
        DATE '2026-05-03',
        TIMESTAMP '2026-05-03 14:00:00',
        TIMESTAMP '2026-05-03 16:00:00',
        'Apresentacao do projeto',
        2
    ),
    (
        3,
        DATE '2026-05-03',
        TIMESTAMP '2026-05-03 08:00:00',
        TIMESTAMP '2026-05-03 10:30:00',
        'Aula pratica',
        3
    ),
    (
        4,
        DATE '2026-05-04',
        TIMESTAMP '2026-05-04 10:00:00',
        TIMESTAMP '2026-05-04 11:30:00',
        'Treinamento interno',
        1
    ),
    (
        5,
        DATE '2026-05-04',
        TIMESTAMP '2026-05-04 13:00:00',
        TIMESTAMP '2026-05-04 17:00:00',
        'Workshop',
        4
    ),
    (
        6,
        DATE '2026-05-05',
        TIMESTAMP '2026-05-05 09:30:00',
        TIMESTAMP '2026-05-05 11:00:00',
        'Alinhamento semanal',
        5
    );

SELECT setval('ambientes_seq', 251, true);
SELECT setval('reservas_seq', 301, true);
