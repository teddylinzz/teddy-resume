package com.teddy.teddyresume

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.teddy.teddyresume.ui.mainmodel.ContactInfo
import com.teddy.teddyresume.ui.mainmodel.ContactTag.Companion.PHONE
import com.teddy.teddyresume.ui.mainmodel.ContactTag.Companion.WEB
import com.teddy.teddyresume.ui.mainmodel.MainViewModel
import com.teddy.teddyresume.ui.mainmodel.Resume
import com.teddy.teddyresume.ui.mainmodel.ResumeLabel
import com.teddy.teddyresume.ui.theme.TeddyResumeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TeddyResumeTheme {
                ResumeView()
            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val resumeJson = readFileFromAssets("resume2023.json")
                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val resume = moshi.adapter(Resume::class.java).fromJson(resumeJson)
                withContext(Dispatchers.Main) {
                    mainViewModel.updateResume(resume!!.information)
                }
            }
        }
    }

    private fun readFileFromAssets(fileName: String): String {
        val inputStream: InputStream = assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.useLines { lines ->
            lines.forEach { stringBuilder.append(it) }
        }
        return stringBuilder.toString()
    }

    @Composable
    fun ResumeView(viewModel: MainViewModel = hiltViewModel()) {
        val information = viewModel.resume
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(information) {
                ResumeBody(information = it)
            }
        }
    }

    @Composable
    fun ResumeBody(information: Resume.Information) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            when (information.label) {
                ResumeLabel.TITLE ->
                    Text(
                        text = information.content,
                        style = MaterialTheme.typography.titleLarge
                    )
                ResumeLabel.CONTACT ->
                    ContactBody(information.contacts)

                ResumeLabel.INTRODUCTION ->
                    Text(
                        text = information.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                ResumeLabel.EXPERIENCE -> ExperienceBody(information = information)
            }
        }
        Divider()
    }

    @Composable
    fun ContactBody(contacts: List<ContactInfo>) {
        Column(Modifier.fillMaxWidth()) {
            contacts.forEach { contact ->
                val annotatedString = buildAnnotatedString {
                    append(contact.title + ": ")
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append(contact.content)
                    }
                }
                ClickableText(text = annotatedString, onClick = {
                    textAction(contact)
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    @Composable
    fun ExperienceBody(information: Resume.Information) {
        Row {
            val drawableId = resources.getIdentifier(information.icon, "drawable", packageName)
            Image(
                modifier = Modifier.size(60.dp),
                painter = painterResource(drawableId),
                contentDescription = "company icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = information.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    private fun textAction(contact: ContactInfo) =
        when (contact.tag) {
            WEB -> {
                val uri = Uri.parse("https://${contact.content}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

            PHONE -> {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${contact.content}")
                startActivity(intent)
            }

            else -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(contact.content))
                startActivity(Intent.createChooser(intent, "Send email"))
            }

        }
}