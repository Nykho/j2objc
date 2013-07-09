/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.j2objc.types;

import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * IOSTypeBinding: synthetic binding for an iOS array type.
 *
 * @author Tom Ball
 */
public class IOSArrayTypeBinding extends IOSTypeBinding {
  private final ITypeBinding elementType;
  private final ITypeBinding primitiveElementType;

  /**
   * Create an array binding.
   *
   * @param name the iOS class name
   * @param elementType the binding for the type of element this array contains
   * @param primitiveElementType the binding for the primitive type corresponding to
   *                   the element this array contains. null for Object[].
   */
  public IOSArrayTypeBinding(
      String name, ITypeBinding elementType, ITypeBinding primitiveElementType) {
    super(name, null, null, false, true);
    this.elementType = elementType;
    this.primitiveElementType = primitiveElementType;
  }

  @Override
  public ITypeBinding getComponentType() {
    return Types.getIOSArrayComponentType(this);
  }

  @Override
  public ITypeBinding getElementType() {
    return elementType;
  }

  @Override
  public boolean isAssignmentCompatible(ITypeBinding toType) {
    if (!toType.isArray()) {
      return false;
    }

    if (toType.getElementType().isPrimitive()) {
      return primitiveElementType != null &&
          primitiveElementType.isAssignmentCompatible(toType.getElementType());
    } else if (primitiveElementType == null) {
      // Object[] - we trust the compiler to already have checked types, since
      // we generalize all non-primitive (or boxed primitive) arrays to
      // Object[]. In Obj-C land, we can always assign arrays of objects.
      return true;
    } else {
      return elementType.isAssignmentCompatible(toType.getElementType());
    }
  }
}
