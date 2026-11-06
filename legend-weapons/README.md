# Legend Weapons (Fabric, Minecraft 1.21.11)

Mod senjata legend dengan efek pasif dan **skill aktif** (klik kanan). Setiap skill punya cooldown
dan mengurangi durability senjata.

| Item | Pasif | Skill aktif (klik kanan) | Cooldown | Durability |
|---|---|---|---|---|
| Pedang Api Abadi (`flame_sword`) | Membakar musuh 4 detik | **Tebasan Api** – dash ±5 blok, membakar musuh di jalur | 12 dtk | -8 |
| Pedang Petir (`thunder_sword`) | Peluang 25% petir saat memukul | **Petir Berantai** – sambaran ke maks. 4 musuh (damage menurun) | 15 dtk | -10 |
| Kapak Penghancur (`legend_axe`) | Knockback + Slowness/Weakness | **Hentakan Bumi** – AoE radius 5, musuh terlempar + Slowness | 18 dtk | -12 |

Skill hanya jalan di sisi server, mengecualikan pemain sendiri, pet, anggota tim, spectator, dan
armor stand. Saat masih cooldown, pesan muncul di action bar. Di creative mode durability tidak
berkurang, tetapi cooldown tetap berjalan.

Semua senjata ada di tab Combat (creative) dan bisa di-craft (lihat `data/legendweapons/recipe/`).
Tooltip item menampilkan nama skill, deskripsi, cooldown, dan biaya durability.

## Menjalankan
Butuh JDK 21.

    ./gradlew runClient     # buka Minecraft dengan mod
    ./gradlew build         # hasil .jar ada di build/libs/

Di Windows pakai `gradlew.bat`. Untuk perintah di server/uji cepat:
`/give @s legendweapons:flame_sword`

## Cara pakai skill
1. Pegang senjata di tangan utama.
2. Klik kanan (tanpa menunduk ke blok) untuk memakai skill.
3. Tunggu cooldown selesai; overlay cooldown muncul di hotbar.

## Struktur
- `src/main/java/.../item/` : registrasi item dan material
- `src/main/java/.../item/custom/` : item senjata (pasif + menghubungkan skill)
- `src/main/java/.../skill/` : framework skill (handler, cooldown, tooltip, helper target)
- `src/main/java/.../skill/impl/` : implementasi tiap skill
- `src/main/resources/assets/legendweapons/` : model, tekstur, nama (lang)
- `src/main/resources/data/legendweapons/recipe/` : resep crafting

Tekstur saat ini placeholder 16x16. Ganti file PNG di `textures/item/` dengan buatan Anda (misalnya
dari Blockbench).

## Catatan pengembangan
- Project ini di-pin ke Fabric Loom `1.17.21` karena Loom 1.18+ mewajibkan JVM 25, sedangkan
  environment build memakai JDK 21.
- Signature API diverifikasi terhadap yarn `1.21.11+build.6`.
