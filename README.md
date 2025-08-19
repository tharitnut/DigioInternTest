# XO Game (Kotlin Jetpack Compose)

โปรเจกต์เกม XO ที่เลือกขนาดกระดานได้เอง (**ตั้งแต่ 3×3 ขึ้นไป**)  
บันทึกประวัติการเล่น (History) ด้วย **Room Database** และเปิดดู **Replay** ได้

---

## 🚀 Features
- เลือกขนาดกระดาน: 3×3, 4×4, 5×5 … (กำหนดได้จากหน้า Home)
- โหมด **2 ผู้เล่นผลัดกัน** (ไม่มี AI)
- บันทึก **Game Session** + **Moves** ลง Room
- หน้า **Replay** ย้อนดูการเดินหมากทีละตา
- UI เขียนด้วย **Jetpack Compose (Material 3)** + **Navigation**

---

## 📂 Project Structure
- `ui/` → ส่วนแสดงผล ( **HomeScreen**, **GameScreen**, **ReplayScreen** )
- `viewmodel/` → State & Logic ของแต่ละหน้า (HomeViewModel, GameViewModel, ReplayViewModel)
- `data/` → ฐานข้อมูล (Room **entities**, **dao**, **db**, **repo**)
- `logic/` → อัลกอริทึมเกม ( **GameEngine** ตรวจผู้ชนะ K-in-a-row )
- `MainActivity.kt` → จุดเริ่มต้นแอพ + Navigation + DI แบบบาง ๆ

---

 ## 🧠 Logic (สรุปย่อ) 
- กติกาชนะ: - 3×3 → **3-in-a-row**
- กระดานใหญ่กว่า → **4-in-a-row** (ปรับได้ใน GameViewModel)
- ตรวจแนวชนะ 4 ทิศ: แนวนอน, แนวตั้ง, ทแยงลง , ทแยงขึ้น

