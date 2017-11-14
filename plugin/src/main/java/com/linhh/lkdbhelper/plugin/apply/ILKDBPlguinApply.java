package com.linhh.lkdbhelper.plugin.apply;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by Linhh on 2017/11/13.
 */

public interface ILKDBPlguinApply {
    public void apply(MethodVisitor mv);
}
