package net.swordie.ms.life.movement;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.Encodable;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.life.Life;
import net.swordie.ms.util.Position;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sjonnie
 * Created on 8/16/2018.
 */
public class MovementInfo implements Encodable {
    private static final Logger log = Logger.getLogger(MovementInfo.class);

    private int unk;
    private int encodedGatherDuration;
    private Position oldPos;
    private Position oldVPos;
    private List<Movement> movements = new ArrayList<>();
    private byte keyPadState;

    public MovementInfo(Position oldPos, Position oldVPos) {
        this.oldPos = oldPos;
        this.oldVPos = oldVPos;
    }

    public MovementInfo(InPacket inPacket) {
        decode(inPacket);
    }

    public void applyTo(Char chr) {
        for (Movement m : getMovements()) {
            m.applyTo(chr);
        }
    }

    public void applyTo(Life life) {
        for (Movement m : getMovements()) {
            m.applyTo(life);
        }
    }

    public void decode(InPacket inPacket) {
        encodedGatherDuration = inPacket.decodeInt(); // 人物是 38 C6 01 00 怪物是 00 00 00 00
        unk = inPacket.decodeInt(); // 00 00 00 00
        oldPos = inPacket.decodePosition();
        oldVPos = inPacket.decodePosition();
        movements = parseMovement(inPacket);
        keyPadState = inPacket.decodeByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(encodedGatherDuration);
        outPacket.encodeInt(unk);
        outPacket.encodePosition(oldPos);
        outPacket.encodePosition(oldVPos);
        outPacket.encodeShort(movements.size());
        for(Movement m : movements) {
            m.encode(outPacket);
        }
        outPacket.encodeByte(keyPadState);
    }

    private static List<Movement> parseMovement(InPacket inPacket) {
        // Taken from mushy when my IDA wasn't able to show this properly
        // Made by Maxcloud
        List<Movement> res = new ArrayList<>();
        int size = inPacket.decodeShort();
        for (int i = 0; i < size; i++) {
            byte type = inPacket.decodeByte();
           // System.err.println("move type " + type);
            switch (type) {
                case 0:
                case 8:
                case 15:
                case 17:
                case 19:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 95:
                    res.add(new MovementNormal(inPacket, type));
                    break;
                case 1:
                case 2:
                case 18:
                case 21:
                case 22:
                case 24:
                case 62:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 100:
                    res.add(new MovementJump(inPacket, type));
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 13:
                case 26:
                case 27:
                case 53:
                case 54:
                case 55:
                case 59:
                case 84:
                case 85:
                case 86:
                case 88:
                case 90:
                case 109:
                    res.add(new MovementTeleport(inPacket, type));
                    break;
                case 12:
                    res.add(new MovementStatChange(inPacket, type));
                    break;
                case 14:
                case 16:
                    res.add(new MovementStartFallDown(inPacket, type));
                    break;
                case 23:
                case 103:
                case 104:
                    res.add(new MovementFlyingBlock(inPacket, type));
                    break;
                case 29: // new 199
                    res.add(new MovementNew1(inPacket, type));
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 51:
                case 52:
                case 56:
                case 58:
                case 60:
                case 61:
                case 63:
                case 64:
                case 78:
                case 79:
                case 80:
                case 82:
                case 87:
                case 89:
                case 91:
                case 92:
                case 93:
                case 94:
                case 96:
                case 97:
                case 98:
                case 99:
                case 101:
                case 102:
                case 105:
                case 106:
                    res.add(new MovementAction(inPacket, type));
                    break;
                case 50:
                    res.add(new MovementOffsetX(inPacket, type));
                    break;
                case 57:
                case 71:
                case 108:
                    res.add(new MovementAngle(inPacket, type)); // probably not a good name
                    break;
                default:
                    log.warn(String.format("Unhandled move path attribute %s. Packet = %s", type, inPacket));
                    res.add(new MovementAction(inPacket, type));
                    break;
            }
        }
        return res;
    }

    public byte getKeyPadState() {
        return keyPadState;
    }

    public List<Movement> getMovements() {
        return movements;
    }
}
