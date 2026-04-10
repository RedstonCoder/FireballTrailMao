package com.maomao.fireballtrail;

import net.minecraft.util.Vec3;

public class FireballUtil {

    public static Vec3 predictLine(Vec3 start, Vec3 motion, int length) {

        Vec3 dir = motion.normalize();

        return new Vec3(
                start.xCoord + dir.xCoord * length,
                start.yCoord + dir.yCoord * length,
                start.zCoord + dir.zCoord * length
        );
    }
}