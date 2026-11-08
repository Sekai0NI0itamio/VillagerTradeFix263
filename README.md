# VillagerTradeFix — Modern Port for Minecraft 26.3 (Fabric)
![Mod loader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=for-the-badge)
![Minecraft: 26.3](https://img.shields.io/badge/Minecraft-26.3-green?style=for-the-badge)
![GitHub license](https://img.shields.io/github/license/Sekai0NI0itamio/VillagerTradeFix263?style=for-the-badge)

**Fixes villager trade abuse.**

The original VillagerTradeFix gave villagers a memory for their
trades so cycling job sites no longer resets trade offers, and
capped reputation discounts. This is a **modern port for Minecraft
26.3 (Fabric)**.

> **Honest port status:** the trade-memory system targeted
> `prepareOffersFor`, which was replaced by `updateTrades` in
> 26.3's rewritten trade flow, and is not ported. The reputation
> cap (max 25) IS preserved via a `getPlayerReputation` mixin.

### Modern port notes (26.3)
- Java 25 · Fabric Loader 0.20.1+ · Fabric API 0.165.0+26.3
- Built and verified in CI via GitHub Actions, including a headless
  runtime test that launches the client, joins a world, and quits cleanly

### Installation
1. Install Fabric Loader for Minecraft 26.3
2. Install Fabric API
3. Drop `villagertradefix263-*.jar` into your `mods` folder

### Original mod & credit
Unofficial modern port by **Itamio** of **VillagerTradeFix** by
**Globox1997** (1.3M downloads, max 1.21.1, MIT).
Original: https://modrinth.com/mod/villagertradefix ·
https://github.com/Globox1997/VillagerTradeFix · See `NOTICE`.
