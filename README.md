# Ambulatorio Medico
***Sistema di Gestione di un Ambulatorio Medico***

**Corso:** Ingegneria dei Sistemi Software - a.a. 2025/26

**Docente:** Domenico Amalfitano

**Corso di Laurea:** Ingegneria Informatica

---

## 👥 Membri del Team
* **Team Lead:** Anna De Maio
* Celeste Sara Avvisato
* Gaia Cozzolino
* Alessia De Michele

---

## 📝 Descrizione del Progetto
Il progetto consiste nello sviluppo di un sistema software per la gestione delle prenotazioni di un ambulatorio medico. Il sistema coinvolge tre tipologie principali di utenti — pazienti, medici e amministratore — ciascuna con funzionalità e permessi specifici.

Le funzionalità principali includono:
* Effettuazione di una prenotazione da parte del paziente, scegliendo specializzazione, medico e fascia oraria disponibile.
* Visualizzazione della disponibilità di medici e specializzazioni, con le relative fasce orarie libere.
* Prevenzione della prenotazione simultanea dello stesso slot orario da parte di più pazienti per la stessa data.
* Visualizzazione dell'elenco delle prenotazioni da parte del medico.
* Monitoraggio dell'andamento dell'ambulatorio da parte dell'amministratore, con conteggi delle prenotazioni per stato, specializzazione e fascia oraria.
* Invio automatico di una notifica di conferma al paziente al termine di una prenotazione andata a buon fine.

---

## 📂 Struttura del Repository
Il progetto è organizzato nelle seguenti directory:
* **Code/**: La directory contenente il progetto Java (Maven, `AmbulatorioMedico2`) con l'implementazione delle funzionalità richieste, organizzato secondo i package `boundary`, `controller`, `entity`, `database`, `setup`.
* **VP/**: Il file `.xmi` del progetto sviluppato tramite Visual Paradigm (diagrammi delle classi, dei casi d'uso, di sequenza e a stati), fonte di verità del modello di dominio.
