package me.kieran.kjcontrol.record;

/*
    Represents a single-entry in the KJControl help menu

    Each HelpEntry defines:
    - The command string to display and run
    - A short description explaining what the command does
    - An optional permission required to see/use the command

    This record allows the help menu to be generated dynamically,
    filtering entries based on the sender's permissions and
    avoiding hard-coded help text.
 */
public record HelpEntry(
        String command,
        String description,
        String permission
) {}
