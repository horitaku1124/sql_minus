// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: exec_result.proto

package com.github.horitaku1124.kotlin.sql_minus_driver.protos;

public final class ExecResultProtos {
  private ExecResultProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ResultOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.Result)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int32 status = 1;</code>
     * @return Whether the status field is set.
     */
    boolean hasStatus();
    /**
     * <code>required int32 status = 1;</code>
     * @return The status.
     */
    int getStatus();

    /**
     * <code>required string body = 2;</code>
     * @return Whether the body field is set.
     */
    boolean hasBody();
    /**
     * <code>required string body = 2;</code>
     * @return The body.
     */
    String getBody();
    /**
     * <code>required string body = 2;</code>
     * @return The bytes for body.
     */
    com.google.protobuf.ByteString
        getBodyBytes();
  }
  /**
   * Protobuf type {@code tutorial.Result}
   */
  public static final class Result extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.Result)
      ResultOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Result.newBuilder() to construct.
    private Result(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Result() {
      body_ = "";
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new Result();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Result(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              bitField0_ |= 0x00000001;
              status_ = input.readInt32();
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              body_ = bs;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ExecResultProtos.internal_static_tutorial_Result_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ExecResultProtos.internal_static_tutorial_Result_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              Result.class, Builder.class);
    }

    private int bitField0_;
    public static final int STATUS_FIELD_NUMBER = 1;
    private int status_;
    /**
     * <code>required int32 status = 1;</code>
     * @return Whether the status field is set.
     */
    @Override
    public boolean hasStatus() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required int32 status = 1;</code>
     * @return The status.
     */
    @Override
    public int getStatus() {
      return status_;
    }

    public static final int BODY_FIELD_NUMBER = 2;
    private volatile Object body_;
    /**
     * <code>required string body = 2;</code>
     * @return Whether the body field is set.
     */
    @Override
    public boolean hasBody() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>required string body = 2;</code>
     * @return The body.
     */
    @Override
    public String getBody() {
      Object ref = body_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          body_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string body = 2;</code>
     * @return The bytes for body.
     */
    @Override
    public com.google.protobuf.ByteString
        getBodyBytes() {
      Object ref = body_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        body_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasStatus()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasBody()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeInt32(1, status_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, body_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, status_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, body_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof Result)) {
        return super.equals(obj);
      }
      Result other = (Result) obj;

      if (hasStatus() != other.hasStatus()) return false;
      if (hasStatus()) {
        if (getStatus()
            != other.getStatus()) return false;
      }
      if (hasBody() != other.hasBody()) return false;
      if (hasBody()) {
        if (!getBody()
            .equals(other.getBody())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasStatus()) {
        hash = (37 * hash) + STATUS_FIELD_NUMBER;
        hash = (53 * hash) + getStatus();
      }
      if (hasBody()) {
        hash = (37 * hash) + BODY_FIELD_NUMBER;
        hash = (53 * hash) + getBody().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static Result parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Result parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Result parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Result parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Result parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Result parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Result parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Result parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static Result parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static Result parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static Result parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Result parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(Result prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code tutorial.Result}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.Result)
        ResultOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ExecResultProtos.internal_static_tutorial_Result_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ExecResultProtos.internal_static_tutorial_Result_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                Result.class, Builder.class);
      }

      // Construct using com.github.horitaku1124.kotlin.sql_minus_driver.protos.ExecResultProtos.Result.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
      public Builder clear() {
        super.clear();
        status_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        body_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ExecResultProtos.internal_static_tutorial_Result_descriptor;
      }

      @Override
      public Result getDefaultInstanceForType() {
        return Result.getDefaultInstance();
      }

      @Override
      public Result build() {
        Result result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public Result buildPartial() {
        Result result = new Result(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.status_ = status_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.body_ = body_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof Result) {
          return mergeFrom((Result)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(Result other) {
        if (other == Result.getDefaultInstance()) return this;
        if (other.hasStatus()) {
          setStatus(other.getStatus());
        }
        if (other.hasBody()) {
          bitField0_ |= 0x00000002;
          body_ = other.body_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        if (!hasStatus()) {
          return false;
        }
        if (!hasBody()) {
          return false;
        }
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        Result parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (Result) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int status_ ;
      /**
       * <code>required int32 status = 1;</code>
       * @return Whether the status field is set.
       */
      @Override
      public boolean hasStatus() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>required int32 status = 1;</code>
       * @return The status.
       */
      @Override
      public int getStatus() {
        return status_;
      }
      /**
       * <code>required int32 status = 1;</code>
       * @param value The status to set.
       * @return This builder for chaining.
       */
      public Builder setStatus(int value) {
        bitField0_ |= 0x00000001;
        status_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 status = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearStatus() {
        bitField0_ = (bitField0_ & ~0x00000001);
        status_ = 0;
        onChanged();
        return this;
      }

      private Object body_ = "";
      /**
       * <code>required string body = 2;</code>
       * @return Whether the body field is set.
       */
      public boolean hasBody() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>required string body = 2;</code>
       * @return The body.
       */
      public String getBody() {
        Object ref = body_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            body_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string body = 2;</code>
       * @return The bytes for body.
       */
      public com.google.protobuf.ByteString
          getBodyBytes() {
        Object ref = body_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          body_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string body = 2;</code>
       * @param value The body to set.
       * @return This builder for chaining.
       */
      public Builder setBody(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        body_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string body = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearBody() {
        bitField0_ = (bitField0_ & ~0x00000002);
        body_ = getDefaultInstance().getBody();
        onChanged();
        return this;
      }
      /**
       * <code>required string body = 2;</code>
       * @param value The bytes for body to set.
       * @return This builder for chaining.
       */
      public Builder setBodyBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        body_ = value;
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:tutorial.Result)
    }

    // @@protoc_insertion_point(class_scope:tutorial.Result)
    private static final Result DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new Result();
    }

    public static Result getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<Result>
        PARSER = new com.google.protobuf.AbstractParser<Result>() {
      @Override
      public Result parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Result(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Result> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Result> getParserForType() {
      return PARSER;
    }

    @Override
    public Result getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_Result_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_Result_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\021exec_result.proto\022\010tutorial\"&\n\006Result\022" +
      "\016\n\006status\030\001 \002(\005\022\014\n\004body\030\002 \002(\tBJ\n6com.git" +
      "hub.horitaku1124.kotlin.sql_minus_driver" +
      ".protosB\020ExecResultProtos"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_Result_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_Result_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_Result_descriptor,
        new String[] { "Status", "Body", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}