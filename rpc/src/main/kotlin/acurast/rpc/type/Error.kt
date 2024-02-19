package acurast.rpc.type

public class UnsupportedApiVersion(version: UInt) : Exception("Acurast RPC API version $version is not supported.")