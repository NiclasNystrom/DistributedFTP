package com.encryption;

import javax.crypto.spec.IvParameterSpec;

public class IVWrapper {
	public byte[] iv;
	public IvParameterSpec spec;

	public IVWrapper(byte[] iv, IvParameterSpec spec) {
		this.iv = iv;
		this.spec = spec;
	}

	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	public IvParameterSpec getSpec() {
		return spec;
	}

	public void setSpec(IvParameterSpec spec) {
		this.spec = spec;
	}
}
