## Tester

### 1. Navigasjon fra hovedmeny til underaktiviteter
- **Beskrivelse**: Denne testen sjekker at "Quiz"-knappen i hovedmenyen navigerer til `QuizActivity`.
- **Forventet resultat**: `QuizActivity` skal åpnes, og `questionTextView` skal vises.
- **Testmetode**: `testNavigationToQuizActivity()` i `MainActivityNavigationTest`.
- **Resultat**: [Passert/Feilet]

### 2. Korrekt oppdatering av score i quizen
- **Beskrivelse**: Denne testen sjekker at scoren oppdateres korrekt når brukeren svarer på et spørsmål.
- **Forventet resultat**: Scoren skal økes med 1 hvis brukeren svarer riktig.
- **Testmetode**: `testScoreUpdate()` i `QuizScoreTest`.
- **Resultat**: [Passert/Feilet]

### 3. Korrekt antall registrerte bilder/personer
- **Beskrivelse**: Denne testen sjekker at antallet oppføringer i galleriet er korrekt etter å ha lagt til eller slettet en oppføring.
- **Forventet resultat**: Antallet oppføringer skal reduseres med 1 etter sletting.
- **Testmetode**: `testEntryCountAfterAddAndDelete()` i `GalleryEntryCountTest`.
- **Resultat**: [Passert/Feilet]
