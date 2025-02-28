package com.codingame.game.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aCat on 2018-03-22.
 */
public class Action {
    public enum Type {SUMMON, ATTACK, USE, PASS}

    public final Type type;
    public final int arg1;
    public final int arg2;
    public final String text; // for say
    public ActionResult result;

    private Action(Type type, int arg1, int arg2, String text) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.text = text;
    }

    public static List<Action> parseSequence(String data) throws InvalidActionHard {
        ArrayList<Action> actions = new ArrayList<>();

        for (String str : data.split(";")) {
            str = str.trim();
            if (str.isEmpty())
                continue; // empty action is a valid action
            actions.add(Action.parse(str));
        }

        return actions;
    }

    // todo copy constructor?

    public static Action parse(String data) throws InvalidActionHard {
        String[] str = data.split(" ", 2);

        Type type;
        switch (str[0].trim()) {
            case "SUMMON":
                type = Type.SUMMON;
                break;
            case "ATTACK":
                type = Type.ATTACK;
                break;
            case "USE":
                type = Type.USE;
                break;
            case "PASS":
                type = Type.PASS;
                break;
            default:
                throw new InvalidActionHard("Invalid action name. Should be SUMMON, ATTACK,  USE or PASS.");
        }

        if (type == Type.SUMMON) {
            if (Constants.LANES == 1) {
                try {
                    String[] args = str[1].split(" ", 2);
                    int arg1;
                    arg1 = Integer.parseInt(args[0]);
                    String text = args.length < 2 ? "" : args[1].trim();
                    return Action.newSummon(arg1, text);
                } catch (Exception e) {
                    throw new InvalidActionHard("Invalid SUMMON argument. Expected integer (card id).");
                }
            }

            try {
                String[] args = str[1].split(" ", 3);
                int arg1;
                int arg2;
                arg1 = Integer.parseInt(args[0]);
                arg2 = Integer.parseInt(args[1]);
                String text = args.length < 3 ? "" : args[2].trim();
                return Action.newSummon(arg1, arg2, text);
            } catch (Exception e) {
                throw new InvalidActionHard("Invalid SUMMON arguments. Expected two integers (card id and lane).");
            }
        } else if (type == Type.PASS) {
            return Action.newPass();
        } else {
            try {
                String[] args = str[1].split(" ", 3);
                int arg1;
                int arg2;
                arg1 = Integer.parseInt(args[0]);
                arg2 = Integer.parseInt(args[1]);
                String text = args.length < 3 ? "" : args[2].trim();
                return type == Type.ATTACK ? Action.newAttack(arg1, arg2, text) : Action.newUse(arg1, arg2, text);
            } catch (Exception e) {
                throw new InvalidActionHard("Invalid " + type.toString() + " arguments. Expected two integers (card id and target id).");
            }
        }
    }

    public static Action newSummon(int arg1) {
        return new Action(Type.SUMMON, arg1, 0, "");
    }

    public static Action newSummon(int arg1, String text) {
        return new Action(Type.SUMMON, arg1, 0, text);
    }

    public static Action newSummon(int arg1, int arg2) {
        return new Action(Type.SUMMON, arg1, arg2, "");
    }

    public static Action newSummon(int arg1, int arg2, String text) {
        return new Action(Type.SUMMON, arg1, arg2, text);
    }

    public static Action newAttack(int arg1, int arg2) {
        return new Action(Type.ATTACK, arg1, arg2, "");
    }

    public static Action newAttack(int arg1, int arg2, String text) {
        return new Action(Type.ATTACK, arg1, arg2, text);
    }

    public static Action newPass() {
        return new Action(Type.PASS, -1, -1, "");
    }

    public static Action newUse(int arg1, int arg2) {
        return new Action(Type.USE, arg1, arg2, "");
    }

    public static Action newUse(int arg1, int arg2, String text) {
        return new Action(Type.USE, arg1, arg2, text);
    }

    public String toStringNoText() {
        switch (type) {
            case SUMMON:
                return (Constants.LANES == 1) ? String.format("SUMMON %d", arg1) : String.format("SUMMON %d %d", arg1, arg2);
            case ATTACK:
                return String.format("ATTACK %d %d", arg1, arg2);
            case USE:
                return String.format("USE %d %d", arg1, arg2);
            case PASS:
                return "PASS";
        }
        return super.toString();
    }

    @Override
    public String toString() {
        switch (type) {
            case SUMMON:
                return (Constants.LANES == 1) ? String.format("SUMMON %d %s", arg1, text) : String.format("SUMMON %d %d %s", arg1, arg2, text);
            case ATTACK:
                return String.format("ATTACK %d %d %s", arg1, arg2, text);
            case USE:
                return String.format("USE %d %d %s", arg1, arg2, text);
            case PASS:
                return "PASS";
        }
        return super.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof Action))
            return false;
        Action a = (Action) other;
        return this.toStringNoText().equals(a.toStringNoText());
    }
}
