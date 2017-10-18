/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package Grafo;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-10-18")
public class Aresta implements org.apache.thrift.TBase<Aresta, Aresta._Fields>, java.io.Serializable, Cloneable, Comparable<Aresta> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Aresta");

  private static final org.apache.thrift.protocol.TField VERTICE1_FIELD_DESC = new org.apache.thrift.protocol.TField("vertice1", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField VERTICE2_FIELD_DESC = new org.apache.thrift.protocol.TField("vertice2", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField PESO_FIELD_DESC = new org.apache.thrift.protocol.TField("peso", org.apache.thrift.protocol.TType.DOUBLE, (short)3);
  private static final org.apache.thrift.protocol.TField DIREC_FIELD_DESC = new org.apache.thrift.protocol.TField("direc", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField DESC_FIELD_DESC = new org.apache.thrift.protocol.TField("desc", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ArestaStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ArestaTupleSchemeFactory();

  public int vertice1; // required
  public int vertice2; // required
  public double peso; // required
  public boolean direc; // required
  public java.lang.String desc; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    VERTICE1((short)1, "vertice1"),
    VERTICE2((short)2, "vertice2"),
    PESO((short)3, "peso"),
    DIREC((short)4, "direc"),
    DESC((short)5, "desc");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // VERTICE1
          return VERTICE1;
        case 2: // VERTICE2
          return VERTICE2;
        case 3: // PESO
          return PESO;
        case 4: // DIREC
          return DIREC;
        case 5: // DESC
          return DESC;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __VERTICE1_ISSET_ID = 0;
  private static final int __VERTICE2_ISSET_ID = 1;
  private static final int __PESO_ISSET_ID = 2;
  private static final int __DIREC_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.VERTICE1, new org.apache.thrift.meta_data.FieldMetaData("vertice1", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    tmpMap.put(_Fields.VERTICE2, new org.apache.thrift.meta_data.FieldMetaData("vertice2", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    tmpMap.put(_Fields.PESO, new org.apache.thrift.meta_data.FieldMetaData("peso", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.DIREC, new org.apache.thrift.meta_data.FieldMetaData("direc", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.DESC, new org.apache.thrift.meta_data.FieldMetaData("desc", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Aresta.class, metaDataMap);
  }

  public Aresta() {
  }

  public Aresta(
    int vertice1,
    int vertice2,
    double peso,
    boolean direc,
    java.lang.String desc)
  {
    this();
    this.vertice1 = vertice1;
    setVertice1IsSet(true);
    this.vertice2 = vertice2;
    setVertice2IsSet(true);
    this.peso = peso;
    setPesoIsSet(true);
    this.direc = direc;
    setDirecIsSet(true);
    this.desc = desc;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Aresta(Aresta other) {
    __isset_bitfield = other.__isset_bitfield;
    this.vertice1 = other.vertice1;
    this.vertice2 = other.vertice2;
    this.peso = other.peso;
    this.direc = other.direc;
    if (other.isSetDesc()) {
      this.desc = other.desc;
    }
  }

  public Aresta deepCopy() {
    return new Aresta(this);
  }

  @Override
  public void clear() {
    setVertice1IsSet(false);
    this.vertice1 = 0;
    setVertice2IsSet(false);
    this.vertice2 = 0;
    setPesoIsSet(false);
    this.peso = 0.0;
    setDirecIsSet(false);
    this.direc = false;
    this.desc = null;
  }

  public int getVertice1() {
    return this.vertice1;
  }

  public Aresta setVertice1(int vertice1) {
    this.vertice1 = vertice1;
    setVertice1IsSet(true);
    return this;
  }

  public void unsetVertice1() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __VERTICE1_ISSET_ID);
  }

  /** Returns true if field vertice1 is set (has been assigned a value) and false otherwise */
  public boolean isSetVertice1() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __VERTICE1_ISSET_ID);
  }

  public void setVertice1IsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __VERTICE1_ISSET_ID, value);
  }

  public int getVertice2() {
    return this.vertice2;
  }

  public Aresta setVertice2(int vertice2) {
    this.vertice2 = vertice2;
    setVertice2IsSet(true);
    return this;
  }

  public void unsetVertice2() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __VERTICE2_ISSET_ID);
  }

  /** Returns true if field vertice2 is set (has been assigned a value) and false otherwise */
  public boolean isSetVertice2() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __VERTICE2_ISSET_ID);
  }

  public void setVertice2IsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __VERTICE2_ISSET_ID, value);
  }

  public double getPeso() {
    return this.peso;
  }

  public Aresta setPeso(double peso) {
    this.peso = peso;
    setPesoIsSet(true);
    return this;
  }

  public void unsetPeso() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __PESO_ISSET_ID);
  }

  /** Returns true if field peso is set (has been assigned a value) and false otherwise */
  public boolean isSetPeso() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __PESO_ISSET_ID);
  }

  public void setPesoIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __PESO_ISSET_ID, value);
  }

  public boolean isDirec() {
    return this.direc;
  }

  public Aresta setDirec(boolean direc) {
    this.direc = direc;
    setDirecIsSet(true);
    return this;
  }

  public void unsetDirec() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __DIREC_ISSET_ID);
  }

  /** Returns true if field direc is set (has been assigned a value) and false otherwise */
  public boolean isSetDirec() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __DIREC_ISSET_ID);
  }

  public void setDirecIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __DIREC_ISSET_ID, value);
  }

  public java.lang.String getDesc() {
    return this.desc;
  }

  public Aresta setDesc(java.lang.String desc) {
    this.desc = desc;
    return this;
  }

  public void unsetDesc() {
    this.desc = null;
  }

  /** Returns true if field desc is set (has been assigned a value) and false otherwise */
  public boolean isSetDesc() {
    return this.desc != null;
  }

  public void setDescIsSet(boolean value) {
    if (!value) {
      this.desc = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case VERTICE1:
      if (value == null) {
        unsetVertice1();
      } else {
        setVertice1((java.lang.Integer)value);
      }
      break;

    case VERTICE2:
      if (value == null) {
        unsetVertice2();
      } else {
        setVertice2((java.lang.Integer)value);
      }
      break;

    case PESO:
      if (value == null) {
        unsetPeso();
      } else {
        setPeso((java.lang.Double)value);
      }
      break;

    case DIREC:
      if (value == null) {
        unsetDirec();
      } else {
        setDirec((java.lang.Boolean)value);
      }
      break;

    case DESC:
      if (value == null) {
        unsetDesc();
      } else {
        setDesc((java.lang.String)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case VERTICE1:
      return getVertice1();

    case VERTICE2:
      return getVertice2();

    case PESO:
      return getPeso();

    case DIREC:
      return isDirec();

    case DESC:
      return getDesc();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case VERTICE1:
      return isSetVertice1();
    case VERTICE2:
      return isSetVertice2();
    case PESO:
      return isSetPeso();
    case DIREC:
      return isSetDirec();
    case DESC:
      return isSetDesc();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof Aresta)
      return this.equals((Aresta)that);
    return false;
  }

  public boolean equals(Aresta that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_vertice1 = true;
    boolean that_present_vertice1 = true;
    if (this_present_vertice1 || that_present_vertice1) {
      if (!(this_present_vertice1 && that_present_vertice1))
        return false;
      if (this.vertice1 != that.vertice1)
        return false;
    }

    boolean this_present_vertice2 = true;
    boolean that_present_vertice2 = true;
    if (this_present_vertice2 || that_present_vertice2) {
      if (!(this_present_vertice2 && that_present_vertice2))
        return false;
      if (this.vertice2 != that.vertice2)
        return false;
    }

    boolean this_present_peso = true;
    boolean that_present_peso = true;
    if (this_present_peso || that_present_peso) {
      if (!(this_present_peso && that_present_peso))
        return false;
      if (this.peso != that.peso)
        return false;
    }

    boolean this_present_direc = true;
    boolean that_present_direc = true;
    if (this_present_direc || that_present_direc) {
      if (!(this_present_direc && that_present_direc))
        return false;
      if (this.direc != that.direc)
        return false;
    }

    boolean this_present_desc = true && this.isSetDesc();
    boolean that_present_desc = true && that.isSetDesc();
    if (this_present_desc || that_present_desc) {
      if (!(this_present_desc && that_present_desc))
        return false;
      if (!this.desc.equals(that.desc))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + vertice1;

    hashCode = hashCode * 8191 + vertice2;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(peso);

    hashCode = hashCode * 8191 + ((direc) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((isSetDesc()) ? 131071 : 524287);
    if (isSetDesc())
      hashCode = hashCode * 8191 + desc.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Aresta other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetVertice1()).compareTo(other.isSetVertice1());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetVertice1()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.vertice1, other.vertice1);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetVertice2()).compareTo(other.isSetVertice2());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetVertice2()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.vertice2, other.vertice2);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetPeso()).compareTo(other.isSetPeso());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPeso()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.peso, other.peso);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetDirec()).compareTo(other.isSetDirec());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDirec()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.direc, other.direc);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetDesc()).compareTo(other.isSetDesc());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDesc()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.desc, other.desc);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Aresta(");
    boolean first = true;

    sb.append("vertice1:");
    sb.append(this.vertice1);
    first = false;
    if (!first) sb.append(", ");
    sb.append("vertice2:");
    sb.append(this.vertice2);
    first = false;
    if (!first) sb.append(", ");
    sb.append("peso:");
    sb.append(this.peso);
    first = false;
    if (!first) sb.append(", ");
    sb.append("direc:");
    sb.append(this.direc);
    first = false;
    if (!first) sb.append(", ");
    sb.append("desc:");
    if (this.desc == null) {
      sb.append("null");
    } else {
      sb.append(this.desc);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ArestaStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ArestaStandardScheme getScheme() {
      return new ArestaStandardScheme();
    }
  }

  private static class ArestaStandardScheme extends org.apache.thrift.scheme.StandardScheme<Aresta> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Aresta struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // VERTICE1
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.vertice1 = iprot.readI32();
              struct.setVertice1IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // VERTICE2
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.vertice2 = iprot.readI32();
              struct.setVertice2IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PESO
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.peso = iprot.readDouble();
              struct.setPesoIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // DIREC
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.direc = iprot.readBool();
              struct.setDirecIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // DESC
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.desc = iprot.readString();
              struct.setDescIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Aresta struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(VERTICE1_FIELD_DESC);
      oprot.writeI32(struct.vertice1);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(VERTICE2_FIELD_DESC);
      oprot.writeI32(struct.vertice2);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(PESO_FIELD_DESC);
      oprot.writeDouble(struct.peso);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(DIREC_FIELD_DESC);
      oprot.writeBool(struct.direc);
      oprot.writeFieldEnd();
      if (struct.desc != null) {
        oprot.writeFieldBegin(DESC_FIELD_DESC);
        oprot.writeString(struct.desc);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ArestaTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ArestaTupleScheme getScheme() {
      return new ArestaTupleScheme();
    }
  }

  private static class ArestaTupleScheme extends org.apache.thrift.scheme.TupleScheme<Aresta> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Aresta struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetVertice1()) {
        optionals.set(0);
      }
      if (struct.isSetVertice2()) {
        optionals.set(1);
      }
      if (struct.isSetPeso()) {
        optionals.set(2);
      }
      if (struct.isSetDirec()) {
        optionals.set(3);
      }
      if (struct.isSetDesc()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetVertice1()) {
        oprot.writeI32(struct.vertice1);
      }
      if (struct.isSetVertice2()) {
        oprot.writeI32(struct.vertice2);
      }
      if (struct.isSetPeso()) {
        oprot.writeDouble(struct.peso);
      }
      if (struct.isSetDirec()) {
        oprot.writeBool(struct.direc);
      }
      if (struct.isSetDesc()) {
        oprot.writeString(struct.desc);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Aresta struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.vertice1 = iprot.readI32();
        struct.setVertice1IsSet(true);
      }
      if (incoming.get(1)) {
        struct.vertice2 = iprot.readI32();
        struct.setVertice2IsSet(true);
      }
      if (incoming.get(2)) {
        struct.peso = iprot.readDouble();
        struct.setPesoIsSet(true);
      }
      if (incoming.get(3)) {
        struct.direc = iprot.readBool();
        struct.setDirecIsSet(true);
      }
      if (incoming.get(4)) {
        struct.desc = iprot.readString();
        struct.setDescIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

