# VillagerTradeFix — Full port for Minecraft 1.20.1 (Forge)
![Mod loader: Forge](https://img.shields.io/badge/modloader-Forge-f8801c?style=for-the-badge)
![Minecraft: 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-green?style=for-the-badge)
![GitHub license](https://img.shields.io/github/license/Sekai0NI0itamio/VillagerTradeFix263?style=for-the-badge)

**Fixes villager trade abuse.**

Villagers remember their trades per job site (cycling job sites no
longer resets trade offers) and reputation discounts are capped.
This is a **full port for Minecraft 1.20.1 (Forge)** of
VillagerTradeFix by Globox1997: the original `prepareOffersFor`
logic (Mojang `updateSpecialPrices`) and NBT trade memory work in
full on this version.

### Port notes (1.20.1 Forge)
- Java 17 · Forge 47.4.20 · Official Mojang mappings · Mixin
- Config replaced with a simple JSON file (`config/villagertradefix.json`):
  `baseVillagerOfTheHeroDiscount`, `extraVillagerOfTheHeroDiscount`,
  `maxReputationDiscount`, `maxReputation`
- Trade memory stored as `MerchantOffers` NBT (`Recipes` tag) per job,
  keyed by profession name (persisted across restarts)
- The optional Numismatic Overhaul compat mixin is not included
  (dependency unavailable on this branch)
- Built and verified in CI via GitHub Actions, including a headless
  runtime test that launches the client, joins a world, and quits cleanly

### Installation
1. Install Forge 47.4.20 for Minecraft 1.20.1
2. Drop `villagertradefix-1.20.1-forge-*.jar` into your `mods` folder

### Original mod & credit
Unofficial full port by **Itamio** of **VillagerTradeFix** by
**Globox1997** (MIT). See NOTICE for credit and licensing details.
