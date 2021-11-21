package com.mega.pmds.data;

public enum CommandType {
    WARPTO_DIRECT(0x02), WARPTO_MAP(0x04), CALL(0x0D), LOAD(0x2D), KAOMADO(0x2E), 
    MSG_EXIT(0x30), MSG_PLAIN(0x32), MSG_THOUGHT(0x33), MSG_SPEECH(0x34), MSG_READING(0x35),
    REWARD(0x3C), RENAME(0x3D), RENAME_TEAM(0x3E), 
    STOP_SONG(0x42), PLAY_SONG(0x44), FADE_IN(0x45), FADE_OUT(0x48), PLAY_SOUND(0x4C), STOP_SOUND(0x4D),
    SET_ANIM(0x54), WARP_TO(0x5B), MOVE_NOROTATE(0x62), CHANGE_Z(0x68), MOVE(0x6A), MOVETO_GRID(0x6B), MOVETO_DIRECT(0x7A),
    FACE_DIR(0x8B), ROTATE(0x91),
    VARY_MSG(0xCF),
    CASE(0xD0), DEFAULT(0xD1), QUESTION(0xD5), VARY_QUESTION(0xD8), OPTION(0xD9),
    SLEEP(0xDB), WAIT_SEND(0xE2), WAIT_FLAG(0xE3), SET_FLAG(0xE4), LOOP(0xE7), EXECUTE(0xE8),
    MSG_END(0xE9), RETURN(0xEE), END(0xEF), CLOSE(0xF0), REMVE(0xF1), LABEL(0xF4), START_THREAD(0xF6);

    private int id;

    private CommandType(int idIn) {
        this.id = idIn;
    }

    /**
     * Returns a CommandType object given an 1 byte id of the command. 
     * @param id The one byte ID associated with a command
     * @return The CommandType corresponding with the id or null if non exists yet.
     */
    public static CommandType fromID(int id) {
		for(CommandType type : CommandType.values()) {
			if ((type.id) == (id)) {
				return type;
			}
		}
		return null;
	}
}

