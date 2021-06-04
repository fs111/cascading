/*
 * Copyright (c) 2016-2021 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.tuple.coerce;

import java.lang.reflect.Type;
import java.util.Map;

import cascading.tuple.type.CoercibleType;
import cascading.tuple.type.CoercionFrom;
import cascading.tuple.type.ToCanonical;

import static cascading.tuple.coerce.Coercions.asNonPrimitive;
import static cascading.tuple.coerce.Coercions.asNonPrimitiveOrNull;

/**
 *
 */
public abstract class NumberCoerce<Canonical> extends Coercions.Coerce<Canonical>
  {
  public NumberCoerce( Map<Type, Coercions.Coerce> map )
    {
    super( map );
    }

  @Override
  public <T> ToCanonical<T, Canonical> from( Type from )
    {
    Class fromAsNonPrimitive = asNonPrimitiveOrNull( from );

    if( fromAsNonPrimitive == asNonPrimitive( getCanonicalType() ) )
      return f -> f == null ? forNull() : (Canonical) f;

    if( fromAsNonPrimitive != null && Number.class.isAssignableFrom( fromAsNonPrimitive ) )
      return f -> f == null ? forNull() : asType( (Number) f );

    if( from instanceof CoercibleType )
      {
      CoercionFrom<T, Object> to = ( (CoercibleType<T>) from ).to( getCanonicalType() );
      return f -> (Canonical) to.coerce( f );
      }

    return f -> f == null ? forNull() : parseType( f );
    }

  @Override
  public Canonical coerce( Object value )
    {
    if( value instanceof Number )
      return asType( (Number) value );
    else if( value == null || value.toString().isEmpty() )
      return forNull();
    else
      return parseType( value );
    }

  protected abstract Canonical forNull();

  protected abstract <T> Canonical parseType( T f );

  protected abstract <T> Canonical asType( Number f );
  }
