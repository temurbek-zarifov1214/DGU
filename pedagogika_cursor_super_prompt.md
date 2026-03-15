# 🎓 KOMPARATIVISTIK PEDAGOGIKA — ANDROID MOBIL ILOVASI
## CURSOR AI — TO'LIQ SUPER PROMPT (1:1 Dizayn Spetsifikatsiyasi)

---

## 📌 LOYIHA UMUMIY MA'LUMOTI

**Ilova nomi:** Komparativistik pedagogika fanini o'gatuvchi mobil ilovasi  
**Platform:** Android (minSdk 26, targetSdk 34)  
**Til:** Kotlin + Jetpack Compose  
**Arxitektura:** MVVM + Clean Architecture  
**Navigatsiya:** Jetpack Navigation Component  
**PDF kutubxona:** AndroidPdfViewer yoki PdfRenderer  
**Animatsiya:** Custom Canvas + Lottie  

---

## 🎨 DIZAYN TIZIMI (Design System)

### Ranglar (Colors)
```kotlin
object AppColors {
    // Asosiy gradient ranglar
    val GradientStart = Color(0xFF071535)
    val GradientMid   = Color(0xFF0D2560)
    val GradientEnd   = Color(0xFF081840)

    // Accent
    val AccentBlue    = Color(0xFF378ADD)
    val AccentPurple  = Color(0xFF6B48FF)
    val FABColor      = Color(0xFFFFB300)

    // Glass kartalar
    val GlassWhite    = Color(0x1AFFFFFF)   // 10% white
    val GlassBorder   = Color(0x33FFFFFF)   // 20% white
    val GlassHover    = Color(0x2EFFFFFF)   // 18% white

    // Matn
    val TextPrimary   = Color(0xFFFFFFFF)
    val TextSecondary = Color(0x99FFFFFF)   // 60% white
    val TextHint      = Color(0x66FFFFFF)   // 40% white

    // Navbar
    val NavbarBg      = Color(0xFF071535)   // Gradient bilan bir xil
}
```

### Tipografiya (Typography)
```kotlin
val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 18.sp,
        color      = Color.White,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 13.sp,
        color      = Color.White
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 10.sp,
        color      = Color(0x99FFFFFF)
    )
)
```

### Border Radius
```
Karta: 16dp
Ikonа doirasi: 12dp
Qidiruv: 24dp (pill)
FAB: 16dp
Dialog: 20dp
Telefon frame: 32dp
```

---

## 🌌 INTERAKTIV FON ANIMATSIYASI (BARCHA SAHIFALARDA)

> **Bu animatsiya barcha sahifalarda bir xil fon bo'lib ishlaydi.**  
> Sichqoncha / barmoq tegsa zarrachalar itariladi va glow paydo bo'ladi.

### `ParticleBackgroundView.kt` — Custom Canvas View

```kotlin
package com.pedagogika.ui.background

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*
import kotlin.random.Random

class ParticleBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // --- Konfiguratsiya ---
    private val PARTICLE_COUNT   = 55
    private val CONNECTION_DIST  = 120f
    private val REPEL_RADIUS     = 140f
    private val REPEL_FORCE      = 2.2f
    private val SYMBOL_COUNT     = 10
    private val SYMBOLS          = listOf("✦", "◆", "○", "□", "△", "✧", "◇")

    // Zarracha ranglari (RGB)
    private val PARTICLE_COLORS = listOf(
        Triple(30,  100, 220),
        Triple(60,  130, 255),
        Triple(100, 60,  200),
        Triple(20,  180, 180),
        Triple(80,  160, 255),
        Triple(140, 80,  220),
        Triple(20,  120, 200)
    )

    // --- Ma'lumotlar ---
    private data class Particle(
        var x: Float, var y: Float,
        var vx: Float, var vy: Float,
        var radius: Float,
        val color: Triple<Int,Int,Int>,
        var alpha: Float,
        var pulse: Float,
        val pulseSpeed: Float
    )

    private data class Floater(
        var x: Float, var y: Float,
        val symbol: String,
        val size: Float,
        val vy: Float,
        var alpha: Float,
        var rotation: Float,
        val rotSpeed: Float
    )

    private val particles  = mutableListOf<Particle>()
    private val floaters   = mutableListOf<Floater>()
    private val paint      = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint  = Paint(Paint.ANTI_ALIAS_FLAG)

    // Mouse / touch pozitsiyasi
    private var touchX = -1f
    private var touchY = -1f
    private var targetX = -1f
    private var targetY = -1f

    // Gradient fon
    private var bgGradient: LinearGradient? = null

    init {
        textPaint.typeface = Typeface.DEFAULT
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        isClickable = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        touchX  = w / 2f;  touchY  = h / 2f
        targetX = w / 2f;  targetY = h / 2f

        bgGradient = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            intArrayOf(
                Color.parseColor("#071535"),
                Color.parseColor("#0D2560"),
                Color.parseColor("#081840")
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        initParticles(w, h)
        initFloaters(w, h)
        startLoop()
    }

    private fun initParticles(w: Int, h: Int) {
        particles.clear()
        repeat(PARTICLE_COUNT) { i ->
            val col = PARTICLE_COLORS[i % PARTICLE_COLORS.size]
            particles.add(Particle(
                x          = Random.nextFloat() * w,
                y          = Random.nextFloat() * h,
                vx         = (Random.nextFloat() - .5f) * .7f,
                vy         = (Random.nextFloat() - .5f) * .7f,
                radius     = 2f + Random.nextFloat() * 5f,
                color      = col,
                alpha      = .15f + Random.nextFloat() * .5f,
                pulse      = Random.nextFloat() * PI.toFloat() * 2f,
                pulseSpeed = .02f + Random.nextFloat() * .04f
            ))
        }
    }

    private fun initFloaters(w: Int, h: Int) {
        floaters.clear()
        repeat(SYMBOL_COUNT) { i ->
            floaters.add(Floater(
                x        = Random.nextFloat() * w,
                y        = h + Random.nextFloat() * 120f,
                symbol   = SYMBOLS[i % SYMBOLS.size],
                size     = 16f + Random.nextFloat() * 20f,
                vy       = -(0.6f + Random.nextFloat() * 1f),
                alpha    = .1f + Random.nextFloat() * .25f,
                rotation = Random.nextFloat() * 360f,
                rotSpeed = (Random.nextFloat() - .5f) * 1.2f
            ))
        }
    }

    // --- Render loop ---
    private var running = false
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            update()
            invalidate()
            if (running) handler.postDelayed(this, 16L) // ~60fps
        }
    }

    private fun startLoop() { running = true; handler.post(ticker) }
    private fun stopLoop()  { running = false; handler.removeCallbacks(ticker) }

    override fun onDetachedFromWindow() { super.onDetachedFromWindow(); stopLoop() }
    override fun onAttachedToWindow()   { super.onAttachedToWindow();   if (width > 0) startLoop() }

    // --- Update ---
    private fun update() {
        val w = width.toFloat()
        val h = height.toFloat()

        // Smooth touch follow
        touchX += (targetX - touchX) * .06f
        touchY += (targetY - touchY) * .06f

        particles.forEach { p ->
            // Mouse repel
            val dx = p.x - touchX
            val dy = p.y - touchY
            val d  = sqrt(dx * dx + dy * dy)
            if (d < REPEL_RADIUS && d > 0) {
                val force = (REPEL_RADIUS - d) / REPEL_RADIUS * REPEL_FORCE
                p.vx += (dx / d) * force
                p.vy += (dy / d) * force
            }
            // Friction
            p.vx *= .97f; p.vy *= .97f
            p.x  += p.vx;  p.y  += p.vy
            p.pulse += p.pulseSpeed

            // Wrap
            if (p.x < 0) p.x = w; if (p.x > w) p.x = 0f
            if (p.y < 0) p.y = h; if (p.y > h) p.y = 0f
        }

        floaters.forEach { f ->
            f.y        += f.vy
            f.rotation += f.rotSpeed
            if (f.y < -30f) {
                f.y = h + 20f
                f.x = Random.nextFloat() * w
            }
        }
    }

    // --- Draw ---
    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()

        // 1) Gradient fon
        paint.shader = bgGradient
        paint.alpha  = 255
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        // 2) Touch glow
        if (touchX > 0) {
            val mg = RadialGradient(touchX, touchY, 240f,
                intArrayOf(Color.argb(46, 80, 140, 255), Color.TRANSPARENT),
                floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
            paint.shader = mg; paint.alpha = 255
            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null
        }

        // 3) Ambient orbs
        drawAmbientOrb(canvas, w * .2f, h * .2f, 180f, Color.argb(33, 30, 80, 200))
        drawAmbientOrb(canvas, w * .8f, h * .7f, 160f, Color.argb(26, 80, 30, 180))
        drawAmbientOrb(canvas, w * .5f, h * .5f, 120f, Color.argb(20, 0, 120, 200))

        // 4) Connections
        paint.strokeWidth = .8f
        paint.style = Paint.Style.STROKE
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val dx = particles[i].x - particles[j].x
                val dy = particles[i].y - particles[j].y
                val d  = sqrt(dx * dx + dy * dy)
                if (d < CONNECTION_DIST) {
                    val (r, g, b) = particles[i].color
                    val a = ((1f - d / CONNECTION_DIST) * 46f).toInt()
                    paint.color = Color.argb(a, r, g, b)
                    canvas.drawLine(particles[i].x, particles[i].y,
                                    particles[j].x, particles[j].y, paint)
                }
            }
        }
        paint.style = Paint.Style.FILL

        // 5) Particles + glow halo
        particles.forEach { p ->
            val pulse = sin(p.pulse) * .3f + .7f
            val (r, g, b) = p.color

            // Halo
            val hg = RadialGradient(p.x, p.y, p.radius * 8f,
                intArrayOf(Color.argb((p.alpha * .3f * pulse * 255).toInt(), r, g, b), Color.TRANSPARENT),
                floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
            paint.shader = hg
            canvas.drawCircle(p.x, p.y, p.radius * 8f, paint)
            paint.shader = null

            // Core
            paint.color = Color.argb((p.alpha * pulse * 255).toInt(), r, g, b)
            canvas.drawCircle(p.x, p.y, p.radius * pulse, paint)
        }

        // 6) Floating symbols
        floaters.forEach { f ->
            canvas.save()
            canvas.translate(f.x, f.y)
            canvas.rotate(f.rotation)
            textPaint.textSize  = f.size
            textPaint.color     = Color.argb((f.alpha * 255).toInt(), 150, 190, 255)
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(f.symbol, 0f, f.size / 2f, textPaint)
            canvas.restore()
        }
    }

    private fun drawAmbientOrb(canvas: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        val og = RadialGradient(cx, cy, r, intArrayOf(color, Color.TRANSPARENT),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        paint.shader = og
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null
    }

    // --- Touch ---
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> { targetX = event.x; targetY = event.y }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                targetX = width / 2f; targetY = height / 2f
            }
        }
        return true
    }
}
```

### XML Layout'ga qo'shish (barcha sahifalarda)
```xml
<!-- fragment_home.xml, fragment_list.xml, fragment_pdf.xml — barchasi -->
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- 1) Interaktiv animatsiyali fon -->
    <com.pedagogika.ui.background.ParticleBackgroundView
        android:id="@+id/particleBg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <!-- 2) UI content tepasida -->
    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- ... sahifa kontenti ... -->
    </androidx.constraintlayout.widget.ConstraintLayout>

</FrameLayout>
```

---

## 📱 SAHIFA 1 — BOSH SAHIFA (HomeFragment)

### Layout: `fragment_home.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.pedagogika.ui.background.ParticleBackgroundView
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:paddingTop="48dp">

        <!-- App ikonasi + sarlavha -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:gravity="center"
            android:paddingBottom="20dp">

            <!-- Ikonа (glassmorphism) -->
            <androidx.cardview.widget.CardView
                android:layout_width="64dp"
                android:layout_height="64dp"
                app:cardCornerRadius="20dp"
                app:cardBackgroundColor="#1AFFFFFF"
                app:cardElevation="0dp"
                android:layout_marginBottom="12dp">
                <ImageView
                    android:layout_width="32dp"
                    android:layout_height="32dp"
                    android:layout_gravity="center"
                    android:src="@drawable/ic_graduation_cap"
                    android:tint="#FFFFFF"/>
            </androidx.cardview.widget.CardView>

            <TextView
                android:text="Komparativistik\nPedagogika"
                android:textColor="#FFFFFF"
                android:textSize="16sp"
                android:textStyle="bold"
                android:gravity="center"
                android:lineSpacingExtra="2dp"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

            <TextView
                android:text="O'quv mobil ilovasi"
                android:textColor="#99FFFFFF"
                android:textSize="11sp"
                android:layout_marginTop="4dp"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>
        </LinearLayout>

        <!-- 2x4 Menu Grid -->
        <GridLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:columnCount="2"
            android:rowCount="4"
            android:paddingHorizontal="16dp"
            android:paddingBottom="16dp">

            <!-- Har bir menu item uchun GlassMenuCard komponenti ishlatiladi -->
            <!-- MenuCard 1 -->
            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_book"
                app:cardIconBg="#33FFEB3B"
                app:cardTitle="Maruzalar matni"
                app:cardSubtitle="8 mavzu"
                app:destinationId="@id/maruzalarFragment"/>

            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_clock"
                app:cardIconBg="#2281D4FA"
                app:cardTitle="Amaliy mashg'ulotlar"
                app:cardSubtitle="Topshiriqlar"
                app:destinationId="@id/amaliyFragment"/>

            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_folder"
                app:cardIconBg="#22A5D6A7"
                app:cardTitle="Tarqatma materiallar"
                app:cardSubtitle="Fayllar"
                app:destinationId="@id/tarqatmaFragment"/>

            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_book_open"
                app:cardIconBg="#22CE93D8"
                app:cardTitle="Glossary"
                app:cardSubtitle="Atamalar lug'ati"
                app:destinationId="@id/glossaryFragment"/>

            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_question"
                app:cardIconBg="#22FFB74D"
                app:cardTitle="Oraliq savollar"
                app:cardSubtitle="Test"
                app:destinationId="@id/oraliqFragment"/>

            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_check_circle"
                app:cardIconBg="#224DD0E1"
                app:cardTitle="Yakuniy savollar"
                app:cardSubtitle="Imtihon"
                app:destinationId="@id/yakuniyFragment"/>

            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_document"
                app:cardIconBg="#22FF8A80"
                app:cardTitle="Ilova hujjati"
                app:cardSubtitle="PDF"
                app:destinationId="@id/hujjatFragment"/>

            <com.pedagogika.ui.components.GlassMenuCard
                android:layout_columnWeight="1"
                android:layout_rowWeight="1"
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_margin="6dp"
                app:cardIcon="@drawable/ic_person"
                app:cardIconBg="#22B2EBF2"
                app:cardTitle="Mualliflar haqida"
                app:cardSubtitle="Ma'lumot"
                app:destinationId="@id/mualliflarFragment"/>

        </GridLayout>
    </LinearLayout>
</FrameLayout>
```

### `GlassMenuCard.kt` — Glassmorphism Menu Kartasi
```kotlin
package com.pedagogika.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.animation.ValueAnimator
import androidx.core.content.ContextCompat
import com.pedagogika.R

class GlassMenuCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var cardTitle    = ""
    var cardSubtitle = ""
    var cardIconBg   = 0x1AFFFFFF
    var cardIcon     = 0
    var onCardClick: (() -> Unit)? = null

    private val bgPaint   = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Press animatsiyasi
    private var pressScale = 1f
    private val pressAnimator = ValueAnimator.ofFloat(1f, .96f).apply {
        duration = 120
        addUpdateListener { pressScale = it.animatedValue as Float; invalidate() }
    }

    // Top shimmer line
    private var shimmerX = 0f
    private val shimmerAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 2400
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { shimmerX = it.animatedFraction; invalidate() }
    }

    init {
        shimmerAnimator.start()
        setOnClickListener { onCardClick?.invoke() }
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        val w  = width.toFloat()
        val h  = height.toFloat()
        val r  = 32f

        canvas.save()
        canvas.scale(pressScale, pressScale, w/2, h/2)

        // Glass background
        bgPaint.color = 0x1AFFFFFF
        val path = Path().apply { addRoundRect(RectF(0f,0f,w,h), r, r, Path.Direction.CW) }
        canvas.drawPath(path, bgPaint)

        // Border
        borderPaint.style  = Paint.Style.STROKE
        borderPaint.color  = 0x33FFFFFF
        borderPaint.strokeWidth = 1f
        canvas.drawPath(path, borderPaint)

        // Shimmer line top
        val shimmerGrad = LinearGradient(
            shimmerX * w - 40f, 0f, shimmerX * w + 40f, 0f,
            intArrayOf(Color.TRANSPARENT, 0x55FFFFFF, Color.TRANSPARENT),
            null, Shader.TileMode.CLAMP
        )
        bgPaint.shader = shimmerGrad
        canvas.drawRect(0f, 0f, w, 1.5f, bgPaint)
        bgPaint.shader = null

        // Icon circle
        bgPaint.color = cardIconBg
        canvas.drawRoundRect(RectF(w/2 - 24f, 16f, w/2 + 24f, 64f), 16f, 16f, bgPaint)

        // Title
        textPaint.color     = 0xFFFFFFFF.toInt()
        textPaint.textSize  = 24f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface  = Typeface.DEFAULT_BOLD
        canvas.drawText(cardTitle.take(10), w/2, 90f, textPaint)

        // Subtitle
        textPaint.color    = 0x99FFFFFF.toInt()
        textPaint.textSize = 18f
        textPaint.typeface = Typeface.DEFAULT
        canvas.drawText(cardSubtitle, w/2, 112f, textPaint)

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressAnimator.start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                pressAnimator.reverse()
                if (event.action == MotionEvent.ACTION_UP) performClick()
            }
        }
        return true
    }
}
```

---

## 📋 SAHIFA 2 — MARUZA RO'YXATI (ListFragment)

### Layout: `fragment_list.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.pedagogika.ui.background.ParticleBackgroundView
        android:id="@+id/particleBg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!-- NAVBAR — gradient bilan bir xil rang -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="#00000000"
            android:orientation="horizontal"
            android:paddingHorizontal="16dp"
            android:gravity="center_vertical">

            <ImageButton
                android:id="@+id/btnBack"
                android:layout_width="36dp"
                android:layout_height="36dp"
                android:src="@drawable/ic_arrow_back"
                android:tint="#FFFFFF"
                android:background="@drawable/ripple_glass_circle"/>

            <TextView
                android:id="@+id/tvTitle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Maruzalar matni"
                android:textColor="#FFFFFF"
                android:textSize="16sp"
                android:textStyle="bold"
                android:gravity="center"
                android:layout_marginHorizontal="8dp"/>

            <!-- Agar fayl qo'shish mumkin bo'lsa, "+" bu yerda ham -->
            <View android:layout_width="36dp" android:layout_height="36dp"/>
        </LinearLayout>

        <!-- Qidiruv -->
        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginHorizontal="16dp"
            android:layout_marginBottom="12dp"
            style="@style/GlassSearchField">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etSearch"
                android:hint="Qidirish..."
                android:textColor="#FFFFFF"
                android:textColorHint="#66FFFFFF"
                android:layout_width="match_parent"
                android:layout_height="48dp"/>
        </com.google.android.material.textfield.TextInputLayout>

        <!-- Fayllar ro'yxati -->
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerFiles"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:paddingHorizontal="16dp"
            android:clipToPadding="false"/>

        <!-- Bo'sh holat (empty state) -->
        <LinearLayout
            android:id="@+id/emptyState"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center"
            android:visibility="gone">

            <ImageView
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:src="@drawable/ic_empty_folder"
                android:alpha="0.4"
                android:tint="#FFFFFF"/>

            <TextView
                android:text="Hali fayl qo'shilmagan"
                android:textColor="#66FFFFFF"
                android:textSize="14sp"
                android:layout_marginTop="16dp"/>

            <TextView
                android:text="+ tugmasini bosib fayl qo'shing"
                android:textColor="#44FFFFFF"
                android:textSize="12sp"
                android:layout_marginTop="6dp"/>
        </LinearLayout>

    </LinearLayout>

    <!-- FAB — o'ng pastda -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAdd"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="20dp"
        android:src="@drawable/ic_add"
        app:backgroundTint="#FFB300"
        app:tint="#FFFFFF"
        app:fabSize="normal"
        app:borderWidth="0dp"
        app:elevation="6dp"/>

</FrameLayout>
```

### `FileListAdapter.kt`
```kotlin
class FileListAdapter(
    private val files: List<FileItem>,
    private val onClick: (FileItem) -> Unit
) : RecyclerView.Adapter<FileListAdapter.VH>() {

    inner class VH(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val file = files[position]
        with(holder.binding) {
            tvFileName.text = file.name
            tvFileSize.text = file.sizeLabel
            root.setOnClickListener { onClick(file) }

            // Kirish animatsiyasi
            root.alpha = 0f
            root.translationY = 30f
            root.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((position * 60L))
                .setDuration(300)
                .start()
        }
    }

    override fun getItemCount() = files.size
}
```

### `item_file.xml` — Glassmorphism fayl kartasi
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="10dp"
    app:cardCornerRadius="16dp"
    app:cardBackgroundColor="#1AFFFFFF"
    app:cardElevation="0dp"
    android:foreground="@drawable/ripple_glass">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="14dp"
        android:gravity="center_vertical">

        <!-- PDF ikona -->
        <FrameLayout
            android:layout_width="44dp"
            android:layout_height="44dp"
            android:background="@drawable/bg_icon_glass_blue">
            <ImageView
                android:layout_width="22dp"
                android:layout_height="22dp"
                android:layout_gravity="center"
                android:src="@drawable/ic_pdf"
                android:tint="#FFFFFF"/>
        </FrameLayout>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginStart="12dp"
            android:orientation="vertical">

            <TextView
                android:id="@+id/tvFileName"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="#FFFFFF"
                android:textSize="13sp"
                android:textStyle="bold"
                android:maxLines="1"
                android:ellipsize="end"/>

            <TextView
                android:id="@+id/tvFileSize"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="#66FFFFFF"
                android:textSize="11sp"
                android:layout_marginTop="3dp"/>
        </LinearLayout>

        <ImageView
            android:layout_width="20dp"
            android:layout_height="20dp"
            android:src="@drawable/ic_chevron_right"
            android:tint="#66FFFFFF"/>

    </LinearLayout>

</androidx.cardview.widget.CardView>
```

---

## 📄 SAHIFA 3 — PDF KO'RUV (PdfViewerFragment)

### Layout: `fragment_pdf_viewer.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Fon animatsiyasi -->
    <com.pedagogika.ui.background.ParticleBackgroundView
        android:id="@+id/particleBg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!-- NAVBAR — fon bilan bir xil gradient rang -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="#CC071535"
            android:orientation="horizontal"
            android:paddingHorizontal="16dp"
            android:gravity="center_vertical">

            <ImageButton
                android:id="@+id/btnBack"
                android:layout_width="36dp"
                android:layout_height="36dp"
                android:src="@drawable/ic_arrow_back"
                android:tint="#FFFFFF"
                android:background="@drawable/ripple_glass_circle"/>

            <TextView
                android:id="@+id/tvPdfName"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textColor="#FFFFFF"
                android:textSize="14sp"
                android:textStyle="bold"
                android:gravity="center"
                android:maxLines="1"
                android:ellipsize="middle"
                android:layout_marginHorizontal="8dp"/>

            <!-- Share button -->
            <ImageButton
                android:id="@+id/btnShare"
                android:layout_width="36dp"
                android:layout_height="36dp"
                android:src="@drawable/ic_share"
                android:tint="#FFFFFF"
                android:background="@drawable/ripple_glass_circle"/>
        </LinearLayout>

        <!-- PDF Viewer -->
        <com.github.barteksc.pdfviewer.PDFView
            android:id="@+id/pdfView"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:layout_marginHorizontal="12dp"
            android:layout_marginTop="8dp"/>

        <!-- Sahifa navigatsiyasi -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="48dp"
            android:orientation="horizontal"
            android:gravity="center"
            android:background="#1AFFFFFF"
            android:paddingHorizontal="24dp">

            <ImageButton
                android:id="@+id/btnPrevPage"
                android:layout_width="36dp"
                android:layout_height="36dp"
                android:src="@drawable/ic_chevron_left"
                android:tint="#FFFFFF"
                android:background="@drawable/ripple_glass_circle"/>

            <TextView
                android:id="@+id/tvPageInfo"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textColor="#FFFFFF"
                android:textSize="13sp"
                android:gravity="center"
                android:text="1 / 1 bet"/>

            <ImageButton
                android:id="@+id/btnNextPage"
                android:layout_width="36dp"
                android:layout_height="36dp"
                android:src="@drawable/ic_chevron_right"
                android:tint="#FFFFFF"
                android:background="@drawable/ripple_glass_circle"/>
        </LinearLayout>
    </LinearLayout>

</FrameLayout>
```

---

## ➕ FAB — FAYL QO'SHISH DIALOGI

### `AddFileDialog.kt` — Fayl nomini so'rash
```kotlin
class AddFileDialog : DialogFragment() {

    var onFileAdded: ((name: String, uri: Uri) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_file, null)

        val etName    = view.findViewById<TextInputEditText>(R.id.etFileName)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnChoose = view.findViewById<Button>(R.id.btnChooseFile)
        val btnAdd    = view.findViewById<Button>(R.id.btnAdd)

        var selectedUri: Uri? = null

        // PDF tanlaش
        val launcher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                selectedUri = it
                // Fayl nomini avtomatik to'ldirish
                if (etName.text.isNullOrBlank()) {
                    val fileName = getFileName(requireContext(), it)
                    etName.setText(fileName?.removeSuffix(".pdf"))
                }
            }
        }

        btnChoose.setOnClickListener {
            launcher.launch("application/pdf")
        }

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val uri  = selectedUri

            when {
                name.isEmpty() -> etName.error = "Iltimos, nom kiriting"
                uri == null    -> Toast.makeText(context, "Fayl tanlanmagan", Toast.LENGTH_SHORT).show()
                else           -> { onFileAdded?.invoke(name, uri); dismiss() }
            }
        }

        btnCancel.setOnClickListener { dismiss() }

        return AlertDialog.Builder(requireContext(), R.style.GlassDialog)
            .setView(view)
            .create()
            .also { it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }
        return null
    }
}
```

### `dialog_add_file.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="@drawable/bg_glass_dialog"
    android:padding="24dp">

    <TextView
        android:text="Fayl qo'shish"
        android:textColor="#FFFFFF"
        android:textSize="18sp"
        android:textStyle="bold"
        android:layout_marginBottom="20dp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

    <!-- PDF tanlash tugmasi -->
    <Button
        android:id="@+id/btnChooseFile"
        android:text="PDF fayl tanlang"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="@drawable/bg_glass_button"
        android:textColor="#FFFFFF"
        android:layout_marginBottom="16dp"/>

    <!-- Nom kiritish -->
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Fayl nomi (masalan: 1-Maruza)"
        android:layout_marginBottom="20dp"
        style="@style/GlassInputLayout">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etFileName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="#FFFFFF"
            android:inputType="text"
            android:imeOptions="actionDone"/>
    </com.google.android.material.textfield.TextInputLayout>

    <!-- Tugmalar -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="end"
        android:gap="12dp">

        <Button
            android:id="@+id/btnCancel"
            android:text="Bekor"
            android:layout_width="wrap_content"
            android:layout_height="40dp"
            android:background="@drawable/bg_glass_button_outline"
            android:textColor="#AAFFFFFF"/>

        <Button
            android:id="@+id/btnAdd"
            android:text="Qo'shish"
            android:layout_width="wrap_content"
            android:layout_height="40dp"
            android:background="@drawable/bg_fab_button"
            android:textColor="#FFFFFF"/>
    </LinearLayout>

</LinearLayout>
```

---

## 🎨 DRAWABLE RESURSLAR

### `bg_glass_card.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#1AFFFFFF"/>
    <stroke android:width="1dp" android:color="#33FFFFFF"/>
    <corners android:radius="16dp"/>
</shape>
```

### `bg_glass_dialog.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#E6071535"/>
    <stroke android:width="1dp" android:color="#33FFFFFF"/>
    <corners android:radius="20dp"/>
</shape>
```

### `bg_fab_button.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <shape><solid android:color="#FFA000"/><corners android:radius="12dp"/></shape>
    </item>
    <item>
        <shape><solid android:color="#FFB300"/><corners android:radius="12dp"/></shape>
    </item>
</selector>
```

### `ripple_glass.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="#33FFFFFF">
    <item android:id="@android:id/mask">
        <shape><solid android:color="#FFFFFF"/><corners android:radius="16dp"/></shape>
    </item>
</ripple>
```

### `ripple_glass_circle.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="#33FFFFFF">
    <item android:id="@android:id/mask">
        <shape android:shape="oval"><solid android:color="#FFFFFF"/></shape>
    </item>
</ripple>
```

---

## ⚙️ TEXNIK TALABLAR

### `build.gradle (app)`
```groovy
dependencies {
    // PDF Viewer
    implementation 'com.github.barteksc:android-pdf-viewer:3.2.0-beta.1'

    // Lottie (ixtiyoriy animatsiya uchun)
    implementation 'com.airbnb.android:lottie:6.1.0'

    // Room (fayllar bazasi)
    implementation 'androidx.room:room-runtime:2.6.1'
    kapt 'androidx.room:room-compiler:2.6.1'

    // Navigation
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.5'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.5'

    // Material
    implementation 'com.google.android.material:material:1.11.0'

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

### `AndroidManifest.xml` — Ruxsatlar
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32"/>
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>

<!-- Fayllar uchun FileProvider -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths"/>
</provider>
```

---

## 📐 STATUS BAR & SYSTEM UI

```kotlin
// Barcha Fragment/Activity'da
fun setupTransparentStatusBar() {
    requireActivity().window.apply {
        statusBarColor = Color.TRANSPARENT
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}
```

---

## ✅ IMPLEMENTATSIYA TARTIBI (Cursor uchun)

```
1. ParticleBackgroundView.kt — Barcha sahifalar uchun umumiy fon
2. AppColors + AppTheme — Dizayn tizimi
3. GlassMenuCard.kt — Bosh sahifa menu kartasi
4. HomeFragment + fragment_home.xml
5. FileListAdapter + item_file.xml
6. ListFragment + fragment_list.xml
7. AddFileDialog + dialog_add_file.xml
8. PdfViewerFragment + fragment_pdf_viewer.xml
9. Room Database — FileItem entity
10. Navigation graph — nav_graph.xml
11. Barcha drawable resurslar
12. Status bar transparent sozlash
```

---

## 🔑 ASOSIY QOIDALAR (Cursor eslab qolsin)

| Qoida | Tavsif |
|---|---|
| **Bir xil fon** | BARCHA sahifalarda `ParticleBackgroundView` ishlatilsin |
| **Navbar rangi** | `#CC071535` — gradient bilan bir xil, navbar hech qachon oq yoki kulrang bo'lmasin |
| **Glassmorphism** | `#1AFFFFFF` fon, `#33FFFFFF` border, backdrop-blur effekti |
| **FAB dialog** | `+` tugmasi bosilganda avval fayl nomini so'raydigan dialog chiqsin |
| **Bo'sh holat** | Fayl yo'q bo'lganda "Hali fayl qo'shilmagan" empty state ko'rinsin |
| **Animatsiya** | Zarrachalar sichqoncha/barmoqdan itariladi, glow effekti chiqadi |
| **Ripple** | Barcha bosish effektlari `#33FFFFFF` shaffof oq ripple |
| **FAB rangi** | `#FFB300` — sariq-to'q sariq, hamma sahifada bir xil |
| **Karta animatsiya** | RecyclerView'da har bir karta pastdan yuqoriga `staggered` kirishi |
| **Status bar** | Transparent, gradient fon status bar tagida ham ko'rinsin |

---

*Bu prompt Cursor AI uchun yozilgan. Har bir kod bloki to'g'ridan-to'g'ri loyihaga joylashtiriladigan darajada to'liq.*
