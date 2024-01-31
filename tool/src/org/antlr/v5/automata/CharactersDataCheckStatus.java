package org.antlr.v5.automata;

public class CharactersDataCheckStatus {
	public final boolean collision;
	public final boolean notImpliedCharacters;

	public CharactersDataCheckStatus(boolean collision, boolean notImpliedCharacters) {
		this.collision = collision;
		this.notImpliedCharacters = notImpliedCharacters;
	}
}
