Resource Elements
-
    - gray
      . Cannot be combined
      . Can only be converted to energy
      . Do we need this?
    0 - white  (common)    value = 1
      . 
    1 - green  (uncommon)  value = 3
    2 - blue   (rare)      value = 11
    3 - purple (epic)      value = 23
    4 - orange (legendary) value = 47
    5 - pink   (cosmic)    value = 101
 
 Inventory
   - Energy
   - Element by type (maybe array[6], store in DB in separate columns)
   

---

Adventures and Encounters
-

Player
- Card 0
- Card 1
- Card 2
- Card 3
- Card 4
  
Adventure (requires at least 3 cards)
- Player card order (Cards over 3 reduce reward)
  - Card 0
  - Card 1
  - Card 2
  - Card 3
  - Card 4
- Encounter 0
  - Enemy 00
  - Enemy 01
- Encounter 1  
  - Enemy 10
  - Enemy 11
  - Enemy 12
- Encounter 2  
  - Enemy 20
  

Combat no casualties
- Enemy 00 * Card 0
- Enemy 01 * Card 1
- Enemy 10 * Card 0
- Enemy 11 * Card 1
- Enemy 12 * Card 2
- Enemy 20 * Card 0

Combat with casualties
- Enemy 00 * Card 0 (lose)
- Enemy 00 * Card 1 (win)
- Enemy 01 * Card 1 (win)
- Enemy 10 * Card 1 (lose)
- Enemy 10 * Card 2 (lose)
- Enemy 10 * Card 2 (win)
- Enemy 11 * Card 2 (lose)
- Enemy 11 * Card 3 (win)
- Enemy 12 * Card 4 (win)
- Enemy 20 * Card 4 (win)
