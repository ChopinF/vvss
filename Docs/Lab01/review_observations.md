# Lab01 ReviewReport — Observații Inspecție

## Date echipă
- **Nume studenti:** [Prenume Nume 1], [Prenume Nume 2]
- **Grupa:** [Grupa]
- **Data realizării:** 09.03.2026
- **Titlu temă:** Lab01 — Inspectare (MyDrinkShop)

---

## 1. Cerințe — Requirements_v1.0.pdf
**Checklist folosit:** Lab01_RequirementsPhaseDefectsChecklist.pdf

| Nr. | Cod | Defect | Observație |
|-----|-----|--------|-----------|
| 1 | R01 | Requirements are incomplete | Cerința 5 (gestionare stocuri) nu specifică comportamentul sistemului când stocul este insuficient la plasarea unei comenzi — lipsește descrierea răspunsului așteptat |
| 2 | R02 | Requirements are missing | Lipsesc complet cerințele de validare a datelor de input (ex: prețul negativ, numele produsului gol, cantitate zero) |
| 3 | R03 | Requirements are incorrect | Cerința 4: "fiecare rețetă este compusă din **mai multe** ingrediente" — "mai multe" implică minim 2, dar logic ar trebui să fie posibil și cu un singur ingredient |
| 4 | R04 | Initialization not considered | Nu se specifică starea inițială a sistemului — fișierele de date pot fi goale la prima rulare, comportamentul nespecificat |
| 5 | R07 | Environment info missing | Nu se specifică versiunea minimă de Java/JavaFX necesară, nici sistemul de operare suportat |

---

## 2. Arhitectură — Diagram_v1.0.pdf
**Checklist folosit:** Lab01_ArchitecturalDesignPhaseDefectsChecklist.pdf

> **Nota:** fișierul `Diagram_v1.0.pdf` lipsește din proiect — diagramă inexistentă.
> Analiza arhitecturii s-a realizat pe baza codului sursă.

| Nr. | Cod | Defect | Observație |
|-----|-----|--------|-----------|
| 1 | A03 | Architecture doesn't cover all requirements | Cerința funcțională 3 ("utilizatorul poate adăuga/modifica/șterge tipuri și categorii") nu se reflectă în arhitectură — `CategorieBautura` și `TipBautura` sunt enum-uri fixe, fără operații CRUD |
| 2 | A05 | No coherent error handling strategy | Nu există strategie coerentă de tratare a erorilor: unele metode aruncă `IllegalStateException`, altele `ValidationException`, fără o ierarhie sau convenție definită |
| 3 | A07 | Class names inconsistent | Inconsistență de nomenclatură: `Reteta`, `Stoc` sunt în română, iar `Order`, `Product` în engleză — lipsă standard uniform |
| 4 | A10 | Entity relationships inconsistent with requirements | `DrinkShopService.comandaProdus()` consumă stocul dar nu înregistrează o comandă în `Order` — fluxul comenzii este incomplet față de cerințele funcționale |

---

## 3. Cod sursă
**Checklist folosit:** Lab01_ProgramCodingPhaseDefectsChecklist.pdf

| Nr. | Cod | Defect | Fișier:Linie | Observație |
|-----|-----|--------|-------------|-----------|
| 1 | C01 | Decision logic erroneous | `DrinkShopService.java:84` | `retetaService.findById()` poate returna `null` dacă produsul nu are rețetă asociată — nu există verificare null înainte de apelul `stocService.areSuficient(reteta)`, rezultând NullPointerException necontrolat |
| 2 | C06 | Errors in processing input data | `FileProductRepository.java:23` | `extractEntity()` nu gestionează linii malformate din fișier — dacă linia are mai puțin de 5 câmpuri, aruncă `ArrayIndexOutOfBoundsException` necontrolat |
| 3 | C08 | Error message processing errors | `FileAbstractRepository.java:26,40` | `IOException` la citire/scriere fișier este tratată doar cu `e.printStackTrace()` — utilizatorul nu primește nicio notificare la eșecul persistenței datelor |
| 4 | C12 | Variable type incorrectly used | `StocService.java:73` | Cast incorect: `s.setCantitate((int)(s.getCantitate() - deScazut))` — `setCantitate` acceptă `double`, cast-ul la `int` este inutil și trunchiază precizia cantității |
