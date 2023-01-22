DROP SCHEMA progettotiw;
SET @@global.time_zone = '+00:00';


CREATE SCHEMA IF NOT EXISTS progettotiw;
USE progettotiw;

CREATE TABLE IF NOT EXISTS User (
    username VARCHAR(25) PRIMARY KEY,
    mail VARCHAR(89) UNIQUE NOT NULL,
    password VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS Photo (
    ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(40) NOT NULL,
    upload_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    alt_text TEXT,
    path VARCHAR(256) NOT NULL,
    IDUser VARCHAR(25) NOT NULL REFERENCES User.username ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Album (
	ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(40) NOT NULL,
    creation_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    sort_id INT NOT NULL DEFAULT 0,
    IDUser VARCHAR(25) NOT NULL REFERENCES User.username ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Comment (
	ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    text TEXT NOT NULL,
    tstamp TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    IDUser VARCHAR(25) NOT NULL REFERENCES User.username ON UPDATE CASCADE ON DELETE CASCADE,
    IDPhoto INT NOT NULL REFERENCES Photo.ID ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PhotoInAlbum (
    IDPhoto INT NOT NULL REFERENCES Photo.ID ON UPDATE CASCADE ON DELETE CASCADE,
    IDAlbum INT NOT NULL REFERENCES Album.ID ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(IDPhoto, IDAlbum)
);

INSERT INTO user VALUES ('andreariboni', 'andrea.riboni@mail.polimi.it', 'Ylil4Ot3KRHU+SvltdsOFFEe2+AdHQ3dHVosuduaVro=');
INSERT INTO user VALUES ('emanuelepaci', 'emanuele.paci@mail.polimi.it', 'Ylil4Ot3KRHU+SvltdsOFFEe2+AdHQ3dHVosuduaVro=');

INSERT INTO album (title, IDUser) VALUES ('Luoghi italiani', 'andreariboni');
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Milano', 'foto del duomo di Milano', 'photos/milano.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (1, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Roma', 'vista del Colosseo', 'photos/roma.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (2, 1);
INSERT INTO comment (text, IDUser, IDPhoto) VALUES ('Ci sono stato 4 anni fa', 'emanuelepaci', 2);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Torino', 'la Mole Antonelliana', 'photos/torino.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (3, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Firenze', 'dettaglio del Duomo di Firenze', 'photos/firenze.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (4, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Napoli', 'Il golfo di Napoli con il Vesuvio', 'photos/napoli.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (5, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Cagliari', 'una tipica spiaggia sarda', 'photos/cagliari.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (6, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Merano', 'i mercatini di Natale', 'photos/merano.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (7, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Bergamo', 'Bergamo alta di notte', 'photos/bergamo.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (8, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Genova', 'una delle vasche dell\'acquario di Genova', 'photos/genova.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (9, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Lucca', 'la fiera \'Lucca Comics\'', 'photos/lucca.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (10, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Ancona', 'il porto di Ancona', 'photos/ancona.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (11, 1);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Bari', 'il centro storico di Bari', 'photos/bari.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (12, 1);

INSERT INTO album (title, IDUser) VALUES ('Raccolta Ukiyo-e', 'andreariboni');
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('The Great Wave off Kanagawa', 'The Great Wave off Kanagawa. Hokusai (1820-1831)', 'photos/hokusai.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (13, 2);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Three Beauties of the Present Day', 'Three Beauties of the Present Day. Utamaro (1793)', 'photos/utamaro.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (14, 2);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Cherry-Blossom Viewing at Asuka Hill', 'Cherry-Blossom Viewing at Asuka Hill. Hiroshige (1840-1842)', 'photos/hiroshige.png', 'andreariboni');
INSERT INTO photoinalbum VALUES (15, 2);


INSERT INTO album (title, IDUser) VALUES ('UK universities', 'emanuelepaci');
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Oxford', 'University of Oxford', 'photos/oxford.png', 'emanuelepaci');
INSERT INTO photoinalbum VALUES (16, 3);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Cambridge', 'University of Cambridge', 'photos/cambridge.png', 'emanuelepaci');
INSERT INTO photoinalbum VALUES (17, 3);

INSERT INTO album (title, IDUser) VALUES ('Dessert', 'emanuelepaci');
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Oreo Cheesecake', 'Cheesecake-oreo fatta in casa', 'photos/oreo.png', 'emanuelepaci');
INSERT INTO photoinalbum VALUES (18, 4);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Red Velvet', 'Red Velvet appena uscita di pasticceria', 'photos/redvelvet.png', 'emanuelepaci');
INSERT INTO photoinalbum VALUES (19, 4);
INSERT INTO photo (title, alt_text, path, IDUser) VALUES ('Colomba', 'La colomba di Iginio Massari', 'photos/colomba.png', 'emanuelepaci');
INSERT INTO photoinalbum VALUES (20, 4);
