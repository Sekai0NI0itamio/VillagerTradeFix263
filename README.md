# VillagerTradeFix — Full port for Minecraft 1.21.1 (Fabric)
![Mod loader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=for-the-badge)
![Minecraft: 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-green?style=for-the-badge)
![GitHub license](https://img.shields.io/github/license/Sekai0NI0itamio/VillagerTradeFix263?style=for-the-badge)

**Fixes villager trade abuse.**

Villagers remember their trades per job site (cycling job sites no
longer resets trade offers) and reputation discounts are capped.
This is a **full port for Minecraft 1.21.1 (Fabric)** of
VillagerTradeFix by Globox1997: the original `prepareOffersFor`
logic and NBT trade memory work in full on this version.

### Port notes (1.21.1)
- Java 21 · Fabric Loader 0.16.14+ · Fabric API 0.116.15+1.21.1 · Yarn mappings
- Config replaced with a simple JSON file (`config/villagertradefix263.json`):
  `baseVillagerOfTheHeroDiscount`, `extraVillagerOfTheHeroDiscount`,
  `maxReputationDiscount`, `maxReputation`
- The optional Numismatic Overhaul compat mixin is not included
  (dependency unavailable on this branch)
- Built and verified in CI via GitHub Actions, including a headless
  runtime test that launches the client, joins a world, and quits cleanly

### Installation
1. Install Fabric Loader for Minecraft 1.21.1
2. Install Fabric API
3. Drop `villagertradefix263-*.jar` into your `mods` folder

### Original mod & credit
Unofficial full port by **Itamio** of **VillagerTradeFix** by
**Globox1997** (MIT). See NOTICE for credit and licensing details.
