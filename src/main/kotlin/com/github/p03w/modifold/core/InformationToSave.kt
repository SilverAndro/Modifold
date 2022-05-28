package com.github.p03w.modifold.core

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.CheckboxViewOptions
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptCheckbox
import com.github.kinquirer.components.promptList
import java.util.*

fun getFilesToTransfer(): FileSet {
    val choice = KInquirer.promptList(
        "What set of files should be transferred?",
        FileSet.values().map { it.userFacing },
        viewOptions = ListViewOptions(questionMarkPrefix = "")
    )

    return FileSet.values().first { it.userFacing == choice }
}

enum class FileSet(val userFacing: String) {
    ALL("All files"),
    LATEST("Only the \"latest files\" (according to curseforge)")
}

fun getInfoToSaveLocally(): EnumSet<InformationToSave> {
    val set = EnumSet.noneOf(InformationToSave::class.java)

    val choices = KInquirer.promptCheckbox(
        "What data should be written to disk locally while transferring?",
        InformationToSave.values().map { it.userFacing },
        hint = "Space to select option, enter to confirm",
        viewOptions = CheckboxViewOptions(questionMarkPrefix = "", unchecked = "[ ] ", checked = "[X] ")
    )

    choices.forEach {
        set.add(when(it) {
            "Curseforge images" -> InformationToSave.IMAGES
            "Version files" -> InformationToSave.VERSIONS
            else -> return@forEach
        })
    }

    return set
}

enum class InformationToSave(val userFacing: String) {
    IMAGES("Curseforge images"),
    VERSIONS("Version files")
}
