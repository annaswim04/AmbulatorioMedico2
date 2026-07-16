package entity;

/**
 * Concettualmente il ciclo di vita di una prenotazione è una macchina a stati finiti, con le
 * transizioni ammesse riportate di seguito. Tuttavia, nei casi d'uso attualmente implementati,
 * il comportamento della prenotazione non varia in base allo stato in cui si trova: lo stato è
 * quindi un semplice dato descrittivo, senza logica associata. Per questo motivo è stato scelto
 * un enum, senza applicare il pattern STATE.
 *
 * L'adozione della macchina a stati finiti tramite pattern STATE (ogni stato responsabile delle
 * proprie transizioni ammesse e del comportamento conseguente) è rimandata al seguito dell'eventuale
 * implementazione dei casi d'uso "Aggiorna stato della prenotazione" e "Annulla prenotazione",
 * nei quali il comportamento dipenderà effettivamente dallo stato corrente.
 *
 *   PRENOTATO --annulla-->        ANNULLATO
 *   PRENOTATO --effettua-->       EFFETTUATO
 *   PRENOTATO --segnalaAssenza--> NON_PRESENTATO
 */
public enum StatoPrenotazione {
    PRENOTATO,
    EFFETTUATO,
    ANNULLATO,
    NON_PRESENTATO
}
