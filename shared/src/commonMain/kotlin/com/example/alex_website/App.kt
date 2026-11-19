package com.example.alex_website

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.compose.MarkdownElement
import org.intellij.markdown.MarkdownTokenTypes
import androidx.compose.foundation.text.selection.SelectionContainer
import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.ic_check
import alex_website.shared.generated.resources.ic_copy
import alex_website.shared.generated.resources.profile
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import alex_website.shared.generated.resources.ic_external
import alex_website.shared.generated.resources.ic_location
import kotlinx.coroutines.delay
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class Page(val title: String) {
    About("About me"),
    Cv("CV"),
    Blog("Blog"),
    Research("Research"),
}

@Composable
fun App() {
    val systemDark = isSystemInDarkTheme()
    var darkOverride by remember { mutableStateOf<Boolean?>(null) }
    val dark = darkOverride ?: systemDark
    var page by remember { mutableStateOf(Page.About) }
    val uriHandler = LocalUriHandler.current

    SiteTheme(dark) {
        val colors = MaterialTheme.colorScheme
        Column(Modifier.fillMaxSize().background(colors.background)) {
            Header(
                current = page,
                dark = dark,
                onToggleTheme = { darkOverride = !dark },
                onNavigate = {
                    if (it == Page.Cv) uriHandler.openUri(Profile.CV_URL) else page = it
                },
            )
            HorizontalDivider(color = colors.outlineVariant)
            Box(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    Modifier.widthIn(max = 860.dp).fillMaxWidth().padding(horizontal = 20.dp, vertical = 40.dp),
                ) {
                    when (page) {
                        Page.About, Page.Cv -> AboutPage()
                        Page.Blog -> EmptyPage("Blog", "No posts yet.")
                        Page.Research -> EmptyPage("Research", "Publications coming soon.")
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(current: Page, dark: Boolean, onToggleTheme: () -> Unit, onNavigate: (Page) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val narrow = maxWidth < 640.dp
        val identity = @Composable {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.clip(MaterialTheme.shapes.small).clickable { onNavigate(Page.About) }.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(36.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        Profile.NAME,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(Modifier.width(8.dp))
                ThemeToggle(dark, onToggleTheme)
            }
        }
        val nav = @Composable {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Page.entries.forEach { NavItem(it, it == current) { onNavigate(it) } }
            }
        }
        if (narrow) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                identity()
                Spacer(Modifier.height(8.dp))
                nav()
            }
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                identity()
                nav()
            }
        }
    }
}

@Composable
private fun NavItem(page: Page, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier.clip(MaterialTheme.shapes.small).clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                page.title,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) colors.primary else colors.onSurfaceVariant,
            )
            if (page == Page.Cv) {
                Spacer(Modifier.width(2.dp))
                Icon(
                    painterResource(Res.drawable.ic_external),
                    contentDescription = "opens PDF",
                    Modifier.size(12.dp),
                    tint = colors.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier.height(2.dp).width(20.dp)
                .background(if (selected) colors.primary else Color.Transparent, CircleShape)
        )
    }
}

/** Sun in dark mode (click → light), moon in light mode (click → dark). */
@Composable
private fun ThemeToggle(dark: Boolean, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    val bg = MaterialTheme.colorScheme.background
    Box(
        Modifier.size(36.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(20.dp)) {
            val c = center
            val r = size.minDimension / 2
            if (dark) {
                drawCircle(color, radius = r * 0.42f, center = c)
                repeat(8) { i ->
                    val a = i * PI / 4
                    val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
                    drawLine(color, c + dir * (r * 0.62f), c + dir * (r * 0.95f), strokeWidth = r * 0.14f)
                }
            } else {
                drawCircle(color, radius = r * 0.8f, center = c)
                drawCircle(bg, radius = r * 0.66f, center = c + Offset(r * 0.38f, -r * 0.32f))
            }
        }
    }
}

@Composable
fun Avatar(size: Dp) {
    Image(
        painterResource(Res.drawable.profile),
        contentDescription = Profile.NAME,
        modifier = Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
        contentScale = ContentScale.Crop,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AboutPage() {
    val colors = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    Avatar(160.dp)
    Spacer(Modifier.height(24.dp))
    Text(Profile.NAME, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        // "Software Engineer, Kotlin @ JetBrains"
        Text("${Profile.ROLE}, ", fontSize = 17.sp, color = colors.onBackground)
        LinkText(Profile.TEAM, Profile.TEAM_URL)
        Text(" @ ", fontSize = 17.sp, color = colors.onSurfaceVariant)
        LinkText(Profile.COMPANY, Profile.COMPANY_URL)
    }
    Spacer(Modifier.height(4.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(Res.drawable.ic_location), contentDescription = null, Modifier.size(16.dp), tint = colors.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(Profile.LOCATION, fontSize = 15.sp, color = colors.onSurfaceVariant)
    }
    Spacer(Modifier.height(24.dp))
    val bodyText = TextStyle(fontSize = 16.sp, lineHeight = 26.sp, color = colors.onBackground)
    Markdown(
        Profile.ABOUT,
        modifier = Modifier.fillMaxWidth(),
        typography = markdownTypography(
            paragraph = bodyText,
            text = bodyText,
            textLink = TextLinkStyles(
                // Always underlined: the accent alone is too close to the body text color (≈1.5:1 in dark theme).
                style = SpanStyle(color = colors.primary, fontWeight = FontWeight.SemiBold),
                hoveredStyle = SpanStyle(background = colors.primary.copy(alpha = 0.15f)),
            ),
        ),
        // The default layout puts a spacer before every node, including the EOL tokens between
        // paragraphs; instead, lay out only the real blocks, exactly one blank line apart.
        success = { state, components, modifier ->
            Column(modifier, verticalArrangement = Arrangement.spacedBy(26.dp)) {
                state.node.children
                    .filter { it.type != MarkdownTokenTypes.EOL }
                    .forEach { MarkdownElement(it, components, state.content, includeSpacer = false) }
            }
        },
    )
    Spacer(Modifier.height(28.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Profile.links.forEach { link ->
            // Icon and its copyable text are grouped so they wrap together.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(colors.surface)
                        .clickable { uriHandler.openUri(link.url) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(link.icon), contentDescription = link.label, Modifier.size(22.dp), tint = colors.onSurface)
                }
                if (link.copyText != null) {
                    Spacer(Modifier.width(8.dp))
                    CopyableContact(link)
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CopyableContact(link: SocialLink) {
    val colors = MaterialTheme.colorScheme
    val text = link.copyText ?: return
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) { delay(1500); copied = false }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        SelectionContainer {
            Text(text, fontSize = 15.sp, color = colors.onBackground)
        }
        Spacer(Modifier.width(4.dp))
        Box(
            Modifier.size(32.dp).clip(CircleShape).clickable { copyToClipboard(text); copied = true },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(if (copied) Res.drawable.ic_check else Res.drawable.ic_copy),
                contentDescription = if (copied) "Copied" else "Copy ${link.label}",
                Modifier.size(16.dp),
                tint = if (copied) colors.primary else colors.onSurfaceVariant,
            )
        }
    }
}

/** Accent-colored text link: hand cursor and underline on hover. */
@Composable
private fun LinkText(text: String, url: String) {
    val uriHandler = LocalUriHandler.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Text(
        text,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary,
        textDecoration = if (hovered) TextDecoration.Underline else null,
        modifier = Modifier
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(interactionSource = interaction, indication = null) { uriHandler.openUri(url) },
    )
}

@Composable
private fun EmptyPage(title: String, message: String) {
    val colors = MaterialTheme.colorScheme
    Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
    Spacer(Modifier.height(16.dp))
    Text(message, fontSize = 16.sp, color = colors.onSurfaceVariant)
}
