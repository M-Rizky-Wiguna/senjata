# Legend Weapons (Fabric, Minecraft 1.21.11)

Mod senjata legend dengan efek pasif, **skill aktif** (klik kanan), dan **skill ultimate**
(sneak + klik kanan) dengan **animasi cinematic ala RPG** di layar. Setiap skill punya cooldown
dan mengurangi durability senjata.

| Item | Pasif | Skill aktif (klik kanan) | Cooldown | Durability |
|---|---|---|---|---|
| Pedang Api Abadi (`flame_sword`) | Membakar musuh 4 detik | **Tebasan Api** – dash ±5 blok, membakar musuh di jalur | 12 dtk | -8 |
| Pedang Petir (`thunder_sword`) | Peluang 25% petir saat memukul | **Petir Berantai** – sambaran ke maks. 4 musuh (damage menurun) | 15 dtk | -10 |
| Kapak Penghancur (`legend_axe`) | Knockback + Slowness/Weakness | **Hentakan Bumi** – AoE radius 5, musuh terlempar + Slowness | 18 dtk | -12 |

## Skill Ultimate (sneak + klik kanan)

Skill jauh lebih kuat dengan animasi cinematic: layar letterbox ala cutscene, nama skill besar
dengan glow warna senjata, flash putih, camera shake, plus suara dan efek dunia yang epik.
Semua pemain dalam radius 64 blok ikut menyaksikan animasinya.

| Item | Ultimate | Efek | Cooldown | Durability |
|---|---|---|---|---|
| Pedang Api Abadi | **Nova Matahari** | Ledakan nova api radius 8: damage besar + bakar 10 dtk + knockback menjauh | 90 dtk | -40 |
| Pedang Petir | **Amarah Badai** | Badai petir radius 12: petir menyambar semua musuh + Slowness | 75 dtk | -35 |
| Kapak Penghancur | **Gempa Penghancur** | Gempa radius 10: musuh terlontar tinggi + Slowness/Weakness kuat (harus di tanah) | 90 dtk | -40 |

Catatan: ultimate dan skill biasa berbagi satu pool cooldown per senjata — setelah memakai
ultimate, skill biasa ikut cooldown panjang (senjata terkuras).

Skill hanya jalan di sisi server, mengecualikan pemain sendiri, pet, anggota tim, spectator, dan
armor stand. Saat masih cooldown, pesan muncul di action bar. Di creative mode durability tidak
berkurang, tetapi cooldown tetap berjalan.

Semua senjata ada di tab Combat (creative) dan bisa di-craft (lihat `data/legendweapons/recipe/`).
Tooltip item menampilkan nama skill, deskripsi, cooldown, dan biaya durability (termasuk ultimate).

## Menjalankan
Butuh JDK 21.

    ./gradlew runClient     # buka Minecraft dengan mod
    ./gradlew build         # hasil .jar ada di build/libs/

Di Windows pakai `gradlew.bat`. Untuk perintah di server/uji cepat:
`/give @s legendweapons:flame_sword`

## Cara pakai skill
1. Pegang senjata di tangan utama.
2. Klik kanan (tanpa menunduk ke blok) untuk memakai skill biasa.
3. Sneak (Shift) + klik kanan untuk memakai **ultimate** dan memutar animasi cinematic.
4. Tunggu cooldown selesai; overlay cooldown muncul di hotbar.

## Struktur
- `src/main/java/.../item/` : registrasi item dan material
- `src/main/java/.../item/custom/` : item senjata (pasif + menghubungkan skill & ultimate)
- `src/main/java/.../skill/` : framework skill & ultimate (handler, scheduler, tooltip, helper target)
- `src/main/java/.../skill/impl/` : implementasi tiap skill & ultimate
- `src/main/java/.../network/` : payload jaringan untuk memicu animasi di klien
- `src/main/java/.../client/` : animasi cinematic (letterbox, judul, flash, camera shake)
- `src/main/resources/assets/legendweapons/` : model, tekstur, nama (lang)
- `src/main/resources/data/legendweapons/recipe/` : resep crafting

Tekstur saat ini placeholder 16x16. Ganti file PNG di `textures/item/` dengan buatan Anda (misalnya
dari Blockbench).

## Catatan pengembangan
- Project ini di-pin ke Fabric Loom `1.17.21` karena Loom 1.18+ mewajibkan JVM 25, sedangkan
  environment build memakai JDK 21.
- Signature API diverifikasi terhadap yarn `1.21.11+build.6`.
- Camera shake diimplementasikan tanpa mixin: offset yaw/pitch dipasang dan dicabut ulang tiap
  frame render dunia, sehingga input mouse pemain tetap berfungsi normal selama guncangan.
