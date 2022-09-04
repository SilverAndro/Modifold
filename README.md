# Modifold

Modifold is a Kotlin CLI utility that allows you to transfer mods from curseforge to modrinth.
**This project is not affiliated with curseforge or modrinth**

### Usage

(Please make sure you are using java 9+, java 8 will not work)

The first thing you need to know is how to get a curseforge project ID. On the sidebar in the "About Project" tab, you can
find that ID. You will need the numerical ID of every curseforge project you want to transfer to modrinth.

![An image showing where the curseforge project ID is located on the project page](images/curseforge_id.png "Curseforge ID location")

You will also need your curseforge username, this is used to verify the ownership of each mod being moved to modrinth,
and will be discarded if it does not match.
(You must be listed as an *author*, it's possible to be listed as a contributor but not an author)

**Make sure 3rd party downloads are enabled on the project**

There is a full `-h` menu, however the simplest invocation of modifold is
simply `java -jar modifold.jar <Owner Name> <Curseforge Project IDs...>`. This will walk you through any necessary
steps, with notices along the way, so please read carefully to avoid confusion. The tool will only move entire
projects, *not* individual files.

Some other common flags would be `-l LICENSE` to set the default license and `-d DISCORD` to set the discord link.
License field will be verified by the modrinth team so make sure you update it, its `arr` by default.

**Remember to update the projects once created!** Modifold creates the mods as drafts so that you can add sources,
issues, delete if needed, ect.


### What next?

If you want to automate publishing to modrinth I recommend the [minotaur](https://github.com/modrinth/minotaur) plugin (maintained by the modrinth team) or [mc-publish](https://github.com/Kir-Antipov/mc-publish).

### FAQ

---
Q: Will you ever add individual file transfer support?

A: No, that's out of scope for this, and im concerned it would make people rely on this tool instead of properly
embracing modrinth.

---

Q: Is this safe to use?

A: Probably! It uses a proxy for the official 3rd party api, so it should be capable of accessing everything in a normal way.

---

Q: Modpack support?

A: Not really viable without much more work than I'm willing to put in, please see the amazing [packwiz](https://github.com/packwiz/packwiz) project for a tool that is mostly capable of that.
