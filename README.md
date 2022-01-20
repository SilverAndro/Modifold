# Modifold

Modifold is a Kotlin CLI utility that allows you to transfer mods from curseforge to modrinth.
**This project is not affiliated with curseforge or modrinth**

### Usage

The first thing you need to know is how to get a curseforge project ID. On this sidebar "About Project" tab, you can find that ID.
You will need the numerical ID of every curseforge project you want to transfer to modrinth.

![An image showing where the curseforge project ID is located on the project page](images/curseforge_id.png "Curseforge ID location")

You will also need your curseforge username, this is used to verify the ownership of each mod being moved to modrinth, and will be discarded if it does not match.
(You must be listed in the authors section)

There is a full `-h` menu, however the simplest invocation of modifold is simply `java -jar modifold.jar <Owner Name> <Curseforge Project IDs...>`.
This will walk you through any necessary steps, with notices along the way, so please read carefully to avoid confusion.
The tool will only move entire projects, *not* individual files.

Some other common flags would be `-l LICENSE` to set the default license and `-d DISCORD` to set the discord link.
License field will be verified by the modrinth team so make sure you update it, its "arr" by default.

**Remember to update the projects once created!** Modifold creates the mods as drafts so that you can add sources, issues, delete if needed, ect.

### FAQ

---
Q: Will you ever add individual file transfer support?

A: No, that's out of scope for this, and im concerned it would make people rely on this tool instead of properly embracing modrinth.

---

Q: Is this safe to use?

A: Probably! It uses the same internal API proxy that other 3rd party tools use. However, this API may be discontinued in
the future, but we'll cross that bridge if we come to it.

---

Q: Modrinth v2 api? / Mod-pack support?

A: When on stable modrinth, this tool will get a rewrite to support those. Support will not be instantaneous, obviously.
