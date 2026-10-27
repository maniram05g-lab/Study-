
package com.mrjat.studyaihindi

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val subjects = listOf(
        "Maths", "Reasoning", "GK",
        "Science", "Hindi", "English"
    )

    private val questions = listOf(
        arrayOf("Maths", "12 × 8 = ?", "84", "96", "108", "86", "1", "12 × 8 = 96"),
        arrayOf("Reasoning", "2, 4, 8, 16, ?", "18", "24", "32", "30", "2", "हर संख्या दोगुनी है"),
        arrayOf("GK", "भारत की राजधानी?", "जयपुर", "नई दिल्ली", "मुंबई", "लखनऊ", "1", "नई दिल्ली भारत की राजधानी है"),
        arrayOf("Science", "पानी का सूत्र?", "CO2", "O2", "H2O", "NaCl", "2", "पानी का सूत्र H2O है"),
        arrayOf("Hindi", "सूर्य का समानार्थी?", "रवि", "निशा", "जल", "पवन", "0", "रवि सूर्य का समानार्थी है"),
        arrayOf("English", "Plural of child?", "childs", "children", "childes", "childrens", "1", "Children is the plural of child")
    )

    private var index = 0
    private var score = 0
    private var seconds = 1800
    private var answered = false
    private var timer: CountDownTimer? = null
    private var selectedSubject = "All Subjects"
    private lateinit var root: LinearLayout
    private lateinit var info: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash()
    }

    private fun splash() {
        val box = LinearLayout(this)
        box.orientation = 1
        box.gravity = Gravity.CENTER
        box.setBackgroundColor(Color.rgb(10, 15, 35))

        val title = TextView(this)
        title.text = "MR JAT"
        title.textSize = 48f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.typeface = Typeface.DEFAULT_BOLD

        val sub = TextView(this)
        sub.text = "STUDY AI HINDI"
        sub.textSize = 20f
        sub.gravity = Gravity.CENTER
        sub.setTextColor(Color.YELLOW)

        box.addView(title)
        box.addView(sub)
        setContentView(box)
        box.postDelayed({ home() }, 3000)
    }

    private fun makeRoot(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = 1
            gravity = Gravity.TOP
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(248, 249, 252))
        }
    }

    private fun addText(
        text: String,
        size: Float = 18f,
        color: Int = Color.DKGRAY
    ) {
        val view = TextView(this)
        view.text = text
        view.textSize = size
        view.setTextColor(color)
        view.setPadding(0, 12, 0, 12)
        root.addView(view)
    }

    private fun addButton(text: String, action: () -> Unit) {
        val button = Button(this)
        button.text = text
        button.isAllCaps = false
        root.addView(button)
        button.setOnClickListener { action() }
    }

    private fun home() {
        root = makeRoot()
        addText("Study AI Hindi", 30f, Color.BLUE)
        addText("MR JAT • आपकी पढ़ाई, आपकी तैयारी")
        addText("विषय चुनें")

        val choices = listOf("All Subjects") + subjects
        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            choices
        )
        root.addView(spinner)

        spinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSubject = choices[position]
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {}
            }

        addButton("टेस्ट शुरू करें") {
            index = 0
            score = 0
            seconds = 1800
            showQuestion()
            startTimer()
        }

        addText("समय: 30 मिनट")
        addText("अभी शुरुआती टेस्ट संस्करण है।")
        setContentView(root)
    }

    private fun filtered() =
        if (selectedSubject == "All Subjects") questions
        else questions.filter { it[0] == selectedSubject }

    private fun showQuestion() {
        val list = filtered()
        if (index >= list.size) {
            result()
            return
        }

        answered = false
        root = makeRoot()
        val q = list[index]

        addText("समय: %02d:%02d".format(seconds / 60, seconds % 60))
        addText("${index + 1} / ${list.size}  •  ${q[0]}", 16f, Color.BLUE)
        addText(q[1], 23f, Color.BLACK)

        for (i in 2..5) {
            val optionIndex = i - 2
            addButton("${'A' + optionIndex}. ${q[i]}") {
                if (!answered) {
                    answered = true
                    val correct = optionIndex == q[6].toInt()
                    if (correct) score++

                    val message = if (correct) {
                        "सही जवाब! ✅"
                    } else {
                        "गलत जवाब। सही उत्तर: ${q[q[6].toInt() + 2]}"
                    }

                    flashMrJat("$message\n${q[7]}")
                }
            }
        }

        info = TextView(this)
        info.textSize = 17f
        info.setTextColor(Color.DKGRAY)
        root.addView(info)

        addButton("अगला प्रश्न →") {
            if (answered) {
                index++
                showQuestion()
            } else {
                info.text = "पहले एक उत्तर चुनो।"
            }
        }

        addButton("टेस्ट समाप्त करें") { result() }
        setContentView(root)
    }

    private fun flashMrJat(message: String) {
        val overlay = TextView(this)
        overlay.text = "MR JAT"
        overlay.textSize = 42f
        overlay.gravity = Gravity.CENTER
        overlay.setTextColor(Color.WHITE)
        overlay.setBackgroundColor(Color.rgb(10, 15, 35))
        setContentView(overlay)

        overlay.postDelayed({
            if (!isFinishing) {
                setContentView(root)
                info.text = message
            }
        }, 700)
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millis: Long) {
                seconds = (millis / 1000).toInt()
            }

            override fun onFinish() {
                seconds = 0
                result()
            }
        }.start()
    }

    private fun result() {
        timer?.cancel()
        root = makeRoot()
        addText("टेस्ट पूरा हुआ!", 30f, Color.BLUE)
        addText("आपके अंक: $score / ${filtered().size}", 24f)
        addButton("होम पर वापस जाएँ") { home() }
        setContentView(root)
    }
}
