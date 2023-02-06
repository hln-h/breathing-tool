package com.example.breathewithme

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import kotlin.streams.toList


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            )
            {
                TypewriterTextSection()
            }

        }
    }
}



@Composable
fun ButtonAnimation(
    startAnimationDuration: Int = 1800,
    breathingAnimationDuration: Int = 7000,
    startScaleDown: Float = 0.3f,
    breatheScaleDown: Float = 0.5F,
    displayButton: Boolean,
) {

    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()
    val scale = remember {
        Animatable(1f)
    }
    var exhaleInhaleColor by remember {
        mutableStateOf(CustomColor.LIGHT_BLUE)
    }
    val animateStateButtonColor =
        buttonColor(exhaleInhaleColor, displayButton)

    var displayButtonText by remember {
        mutableStateOf(true)
    }
    var exhaleInhaleText by remember {
        mutableStateOf("")
    }


    fun resetButton() {
        displayButtonText = true
        exhaleInhaleText = ""
        exhaleInhaleColor = CustomColor.LIGHT_BLUE
    }

    val readyText = stringResource(R.string.ready)
    val inhaleText = stringResource(R.string.inhale)
    val holdText = stringResource(R.string.hold)
    val exhaleText = stringResource(R.string.exhale)
    val endText = stringResource(R.string.praise)
    val restartText = stringResource(R.string.again)

    val delayTime = 3000L

    // Start exhale-inhale sequence
    fun startTimer() {
        coroutineScope.launch {
            yield()
            exhaleInhaleText = readyText
            delay(delayTime)
            repeat(6) {
                exhaleInhaleText = inhaleText
                exhaleInhaleColor = CustomColor.PALE_BLUE
                scale.animateTo(
                    breatheScaleDown,
                    animationSpec = tween(breathingAnimationDuration, easing = FastOutSlowInEasing),
                )
                exhaleInhaleText = holdText
                delay(delayTime)
                exhaleInhaleText = exhaleText
                exhaleInhaleColor = CustomColor.PURPLE
                scale.animateTo(
                    1.2f, animationSpec = tween(
                        breathingAnimationDuration, delayMillis = 200, easing = LinearEasing
                    )
                )
                exhaleInhaleText = holdText
                delay(delayTime)
            }
            exhaleInhaleText = endText
            delay(delayTime)
            exhaleInhaleText = restartText
            this.cancel()
        }
        resetButton()
    }

    Box(
        modifier = if (displayButton) Modifier
            .scale(scale = scale.value)
            .clickable(interactionSource = interactionSource, indication = null) {
                coroutineScope.launch {
                    yield()
                    displayButtonText = false
                    scale.animateTo(
                        startScaleDown,
                        animationSpec = tween(startAnimationDuration, easing = FastOutSlowInEasing),
                    )
                    scale.animateTo(
                        1.2f,
                        animationSpec = tween(
                            startAnimationDuration, delayMillis = 200, easing = LinearEasing
                        ),
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = tween(
                            startAnimationDuration, easing = LinearEasing
                        ),
                    )
                    this.cancel()
                }
                startTimer()
            }
            .padding(10.dp)
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                color = animateStateButtonColor.value,
            ) else Modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (displayButtonText) stringResource(R.string.tap_here) else exhaleInhaleText,
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Center,
            fontWeight = if (displayButtonText) FontWeight.SemiBold else FontWeight.ExtraLight,
            fontSize = if (displayButtonText || exhaleInhaleText == restartText) 20.sp else 55.sp,
            color = Color.White,
        )
    }
}

@Composable
private fun buttonColor(
    exhaleInhaleColor: CustomColor,
    displayButton: Boolean
) = when (exhaleInhaleColor) {
    CustomColor.LIGHT_BLUE -> {
        animateColorAsState(
            targetValue = if (displayButton) Color(CustomColor.LIGHT_BLUE.color) else Color.White,
            animationSpec = tween(2000, 0, LinearEasing)
        )
    }
    CustomColor.PURPLE -> {
        animateColorAsState(targetValue = Color(CustomColor.PURPLE.color),
                animationSpec = tween(1000, 0, LinearEasing))
    }
    CustomColor.PALE_BLUE -> {
        animateColorAsState(targetValue = Color(CustomColor.PALE_BLUE.color),
                animationSpec = tween(1000, 0, LinearEasing))
    }
    else -> {
        animateColorAsState(targetValue = Color(CustomColor.LIGHT_BLUE.color))
    }
}


@Composable
fun TypewriterTextSection() {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        TypewriterText(
            texts = listOf(
                stringResource(R.string.greeting_hello) + "\n" +
                        stringResource(R.string.greeting_question) + "\n\n" +
                        stringResource(R.string.greeting_breathe)
            ),
        )
    }
}

@Composable
fun TypewriterText(
    texts: List<String>,
) {
    var textIndex by remember {
        mutableStateOf(0)
    }
    var textToDisplay by remember {
        mutableStateOf("")
    }
    var displayButton by remember {
        mutableStateOf(false)
    }
    val textCharsList: List<List<String>> = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            texts.map {
                it.splitToCodePoints()
            }
        } else {
            texts.map { text ->
                text.map {
                    it.toString()
                }
            }
        }
    }

    LaunchedEffect(
        key1 = texts,
    ) {
        while (textIndex < textCharsList.size) {
            textCharsList[textIndex].forEachIndexed { charIndex, _ ->
                textToDisplay = textCharsList[textIndex]
                    .take(
                        n = charIndex + 1,
                    ).joinToString(
                        separator = "",
                    )
                Log.e("ERROR", "$charIndex $textToDisplay")
                delay(160)
            }
            textIndex = (textIndex + 1) % texts.size
            delay(1000)
            textIndex++
            displayButton = true
        }
    }

    Text(
        text = textToDisplay,
        fontSize = 20.sp,
        color = Color(CustomColor.GREY_BLUE.color),
        textAlign = TextAlign.Center,
    )
    ButtonAnimation(displayButton = displayButton)

}

@RequiresApi(Build.VERSION_CODES.N)
fun String.splitToCodePoints(): List<String> {
    return codePoints()
        .toList()
        .map {
            String(Character.toChars(it))
        }
}


enum class CustomColor {
    PALE_BLUE,
    LIGHT_BLUE,
    GREY_BLUE,
    PURPLE,
    LIGHT_GREY;

    val color: Long
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            PALE_BLUE -> 0xFFbadaee
            LIGHT_BLUE -> 0xFF8cc2e3
            GREY_BLUE -> 0xFF62879e
            PURPLE -> 0xFFb6bde1
            LIGHT_GREY -> 0xFFdddddd
        }
}