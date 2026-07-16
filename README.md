# Ambulatorio Medico — Sistema Gestionale Prenotazioni

Progetto per l'esame di Ingegneria del Software (Prof. Domenico Amalfitano).
Architettura **BCED** (Boundary - Control - Entity - Database), Java 21 + Maven,
persistenza JPA/Hibernate su **MySQL**, GUI **Swing**.

## Casi d'uso implementati

Solo i 4 concordati (gli altri casi d'uso del sistema completo — login,
registrazione, gestione profili medici, annullamento, aggiornamento stato,
storico paziente — **non** sono implementati):

1. **Visualizzazione disponibilità** (Paziente)
2. **Effettua prenotazione** (Paziente) — combinata con la precedente in
   un unico form, perché nei diagrammi di sequenza è un unico flusso continuo
3. **Visualizzazione elenco prenotazioni** (Medico)
4. **Monitoraggio ambulatorio** (Amministratore)

## Come avviare

### 1. Configura MySQL

In `src/main/resources/META-INF/persistence.xml`, imposta utente/password
del tuo server MySQL locale (di default punta a `root` / `tua PSW` su
`127.0.0.1:3306`, database `ambulatorio_medico` creato automaticamente).

### 2. Prima esecuzione: crea lo schema e i dati di esempio

Esegui `setup.MainInizializzaDatabase`. Con `hibernate.hbm2ddl.auto=create`
(già impostato) le tabelle vengono ricreate da zero e popolate con
specializzazioni, medici, pazienti, un amministratore e alcune prenotazioni
di esempio già in stati diversi.

**Dopo la prima esecuzione**, se vuoi rieseguire i form senza perdere i dati
inseriti manualmente, cambia in `persistence.xml`:
```xml
<property name="hibernate.hbm2ddl.auto" value="update"/>
```

### 3. Testare i singoli casi d'uso

**Non è ancora stata implementata una schermata di login/menu** (rimandata
volutamente). Per testare ciascun caso d'uso ci sono 3 classi `Main` di
comodo in `setup/`, che inizializzano il database, lo popolano, e aprono
direttamente il form giusto usando il primo paziente/medico creato dal seed:

- `setup.MainTestVisualizzaDisponibilita` → apre il form disponibilità + prenotazione
- `setup.MainTestElencoPrenotazioni` → apre l'elenco prenotazioni del primo medico
- `setup.MainTestMonitoraggioAmbulatorio` → apre la dashboard di monitoraggio

Ognuna stampa in console gli id di medici/pazienti disponibili, utili
quando costruirai la vera schermata di login.

## Struttura del progetto (BCED)

```
src/main/java/
├── boundary/    Form Swing (scritti in Java puro, senza file .form)
├── controller/  Facade + DTO/Adapter per la GUI
├── entity/      Entity JPA + Registro* (persistenza/query) + ServiziMonitoraggio
├── database/    Singleton JpaUtil + GestorePersistenza generico
├── stato/       Pattern State (ciclo di vita della Prenotazione)
├── notifica/    Pattern Observer (notifiche al paziente)
├── filtro/      Pattern Composite (criteri di filtro componibili)
└── setup/       Popolamento dati di esempio + Main di test
```

## Mappa dei design pattern richiesti

| Pattern | Classe/i | Ruolo |
|---|---|---|
| **Singleton** | `database.JpaUtil` | Un solo `EntityManagerFactory` per tutta l'app |
| **Facade** | `controller.ControllerPrenotazioni` | Unico punto di ingresso per il Boundary, coordina i Registro* |
| **Adapter** | `controller.OpzioneSelezione`, `RigaPrenotazione`, `DatiMonitoraggio` | Disaccoppiano le entity JPA dalla GUI (mai un'entity arriva al Boundary) |
| **Observer** | `notifica.SoggettoPrenotazioni` (Subject, implementato da `RegistroPrenotazioni`), `notifica.OsservatorePrenotazione` (Observer, implementato da `SistemaDiNotifiche`) | Notifica il paziente alla conferma di una prenotazione |
| **State** | `stato.StatoPrenotazione` + `AppuntamentoPrenotato`/`AppuntamentoEffettuato`/`AppuntamentoAnnullato`/`PazienteNonPresentato` | Ciclo di vita della `Prenotazione`, transizioni legali gestite da ciascuno stato |
| **Composite** | `filtro.CriterioFiltro` + criteri foglia (`FiltroPerMedico`, `FiltroPerStato`, ...) + `FiltroComposito` | Filtri componibili usati sia in "elenco prenotazioni" che in "monitoraggio ambulatorio" |

## Note di progettazione

- **FasciaOraria**: nel diagramma UML era una gerarchia (Mattina/Primo
  pomeriggio/Tardo pomeriggio). È stata implementata come **enum**
  (`entity.TipoFascia`) con orari fissi, perché le tre varianti non
  avevano né attributi né comportamento differenziato — una gerarchia JPA
  avrebbe aggiunto complessità senza reale beneficio (come deciso insieme).
- **Utente → Paziente/Medico/Amministratore**: strategia di mapping
  **JOINED** (tabelle separate collegate da chiave esterna), come deciso.
- Le relazioni `@OneToMany` sono **LAZY** di default (standard JPA); dove
  serve caricarle (es. disponibilità di un medico), i Registro* usano query
  esplicite con `JOIN FETCH` invece di abilitare il caricamento eager
  ovunque.
- Il progetto compila correttamente (verificato con `javac` contro stub
  delle API `jakarta.persistence`); le versioni delle dipendenze in
  `pom.xml` sono state verificate su Maven Central.

## Cosa manca (fuori scope per ora)

- Login/registrazione e la relativa schermata iniziale
- Gestione profili medici da parte dell'amministratore
- Annullamento prenotazione e aggiornamento stato da parte del medico
  (il pattern State li supporta già: `Prenotazione.annulla()` e
  `.segnaNonPresentato()` sono pronti, manca solo il Boundary)
- Storico prenotazioni del paziente
- Hashing della password (il campo esiste ma non è ancora usato da nessun
  caso d'uso implementato)
