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

package cascading.tuple;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import cascading.tuple.coerce.Coercions;
import cascading.tuple.type.CoercibleType;
import cascading.tuple.type.CoercionFrom;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 *
 */
@State(Scope.Thread)
public class CoerceBench
  {
  public enum Canonical
    {
      String,
      Short,
      Short_TYPE,
      Integer,
      Integer_TYPE,
      Long,
      Long_TYPE,
      Float,
      Float_TYPE,
      Double,
      Double_TYPE
    }

  @Param({
    "String",
    "Short",
    "Short_TYPE",
    "Integer",
    "Integer_TYPE",
    "Long",
    "Long_TYPE",
    "Float",
    "Float_TYPE",
    "Double",
    "Double_TYPE"
  })

  CanonicalBench.Canonical to = CanonicalBench.Canonical.String;

  Type[] canonicalTypes = new Type[]{
    String.class,
    Short.class,
    Short.TYPE,
    Integer.class,
    Integer.TYPE,
    Long.class,
    Long.TYPE,
    Float.class,
    Float.TYPE,
    Double.class,
    Double.TYPE
  };

  Object[] canonicalValues = new Object[]{
    "1000",
    1000,
    1000,
    1000,
    1000,
    1000L,
    1000L,
    1000.000F,
    1000.000F,
    1000.000D,
    1000.000D
  };

  Class[] toTypes = new Class[]{
    String.class,
    Integer.class,
    Long.class,
    Float.class,
    Double.class
  };

  CoercibleType coercibleType;
  Object canonicalValue;
  CoercionFrom[] coercions;

  @Setup
  public void setup()
    {
    coercibleType = Coercions.coercibleTypeFor( canonicalTypes[ to.ordinal() ] );
    canonicalValue = canonicalValues[to.ordinal()];
    coercions = new CoercionFrom[ toTypes.length ];

    for( int i = 0; i < toTypes.length; i++ )
      coercions[ i ] = coercibleType.to( toTypes[ i ] );
    }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void baseline( Blackhole bh )
    {
    for( int i = 0; i < toTypes.length; i++ )
      bh.consume( coercibleType.coerce( canonicalValue, toTypes[ i ] ) );
    }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void coercionFrom( Blackhole bh )
    {
    for( int i = 0; i < toTypes.length; i++ )
      bh.consume( coercibleType.to( toTypes[ i ] ).coerce( canonicalValue ) );
    }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void coercionFromFixed( Blackhole bh )
    {
    for( int i = 0; i < toTypes.length; i++ )
      bh.consume( coercions[ i ].coerce( canonicalValue ) );
    }
  }