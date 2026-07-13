# 🏥Ambulatorio Medico
***Sistema di Gestione di un Ambulatorio Medico***

**Corso:** Ingegneria dei Sistemi Software - a.a. 2025/26

**Docente:** <Prof. Domenico Amalfitano>

**Corso di Laurea:** Ingegneria Informatica

---

## 👥 Membri del Team
* **Team Lead:** <Anna De Maio>
* <Celeste Sara Avvisato>
* <Gaia Cozzolino>
* <Alessia De Michele>

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

## 🏗️ Architettura
L'applicazione segue rigorosamente il pattern architetturale **BCED** (Boundary–Controller–Entity–Database), con un flusso di dipendenze **unidirezionale**:

```
boundary  ->  controller  ->  entity (+ Registro*)  ->  database
 (Swing)     (facade UC)     (dominio + logica)        (persistenza JPA)
```

* **boundary** — interfaccia grafica Swing; parla solo con i controller.
* **controller** — orchestrano i casi d'uso e fanno da Facade verso la boundary.
* **entity** — modello di dominio; i `Registro*` incapsulano l'accesso ai dati.
* **database** — unico strato a conoscere JPA/Hibernate (`JpaUtil`, `GestorePersistenza`).

### Design pattern adottati
| Pattern    | Dove |
|------------|------|
| Singleton  | `database.JpaUtil`, `controller.ControllerPrenotazioni`, adapter notifiche |
| Facade     | `database.GestorePersistenza` (su JPA); i controller verso la boundary |
| Adapter    | `boundary.SistemaNotifiche` ↔ COTS Jakarta Mail (`SistemaNotificheJakarta`) |
| State      | `entity.StatoPrenotazione` + stati dell'appuntamento (Prenotato/Effettuato/Annullato/PazienteNonPresentato) |
| Composite  | `entity.FasciaOraria` → `SlotOrario` (foglia), `Mattina/PrimoPomeriggio/TardoPomeriggio/Giornata` (compositi) |

---

## 🛠️ Stack Tecnico
* **Linguaggio:** Java 23
* **Build:** Maven
* **Persistenza:** MySQL via JPA/Hibernate (`hibernate-core`, `mysql-connector-j`)
* **GUI:** Swing (+ `jcalendar` per i date picker)
* **Notifiche (COTS):** Jakarta Mail su SMTP di test (Ethereal)

---

## 📂 Struttura del Repository
Il progetto è organizzato nelle seguenti directory:
* **JavaProject/**: La directory contenente il progetto Java (Maven, `AmbulatorioMedico2`) con l'implementazione delle funzionalità richieste, organizzato secondo i package `boundary`, `controller`, `entity`, `database`, `setup`.
* **VisualParadigm/**: Il file `.vpp` del progetto sviluppato tramite Visual Paradigm (diagrammi delle classi, dei casi d'uso, di sequenza e a stati), fonte di verità del modello di dominio.
* **Documentazione/**:File della documentazione del progetto sia in formato .doc che .pdf, basati sul template fornito dal docente.

---

## ▶️ Avvio
Il progetto è un modulo Maven in `Code/AmbulatorioMedico2/`.

Compilazione (non richiede MySQL attivo):
```bash
cd Code/AmbulatorioMedico2
mvn -q compile
```

Esecuzione (richiede un'istanza MySQL attiva su `localhost:3306`):
```bash
mvn exec:java -Dexec.mainClass=setup.MainSetup
```

La configurazione della persistenza (URL, utente e password del database) si trova
in `src/main/resources/META-INF/persistence.xml`, persistence-unit `ambulatoriomedico`.
Il database `ambulatoriomedico` viene creato automaticamente al primo avvio
(`createDatabaseIfNotExist=true`, `hibernate.hbm2ddl.auto=update`).

---
