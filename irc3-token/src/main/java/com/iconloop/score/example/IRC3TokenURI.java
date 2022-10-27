/*
 * Copyright 2020 ICONLOOP Inc.
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

package com.iconloop.score.example;

import com.iconloop.score.token.irc3.IRC3Basic;
import score.Address;
import score.Context;
import score.DictDB;
import score.VarDB;
import score.annotation.External;

import java.math.BigInteger;

public class IRC3TokenURI extends IRC3Basic {
    private final DictDB<BigInteger, String> tokens;
    private final VarDB<BigInteger> countURI;

    public IRC3TokenURI(String _name, String _symbol) {
        super(_name, _symbol);
        this.tokens = Context.newDictDB("tokens", String.class);
        this.countURI = Context.newVarDB("countURI", BigInteger.class);
        this.countURI.set(BigInteger.ZERO);
    }

    @External
    public void mint(BigInteger _tokenId) {
        Context.require(Context.getCaller().equals(Context.getOwner()));
        super._mint(Context.getCaller(), _tokenId);
    }

    @External
    public void mintURI(String uri) {
        Context.require(Context.getCaller().equals(Context.getOwner()));
        this.countURI.set(this.countURI.get().add(BigInteger.ONE));
        this.tokens.set(this.countURI.get(), uri);
        super._mint(Context.getCaller(), this.countURI.get());
    }

    @External
    public void increaseCount() {
        Context.require(Context.getCaller().equals(Context.getOwner()));
        this.countURI.set(this.countURI.get().add(BigInteger.ONE));
    }

    @External(readonly = true)
    public String getURIToken(BigInteger _tokenId) {
        return this.tokens.get(_tokenId);
    }

    @External
    public void burn(BigInteger _tokenId) {
        Address owner = ownerOf(_tokenId);
        Context.require(Context.getCaller().equals(owner));
        super._burn(_tokenId);
    }

    @External(readonly = true)
    public BigInteger total() {
        return this.countURI.get();
    }
}