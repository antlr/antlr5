// Copyright 2017-present Strumenta and contributors, licensed under Apache 2.0.
// Copyright 2024-present Strumenta and contributors, licensed under BSD 3-Clause.

//
// This lexer is generated from antlr-kotlin-tests/antlr/XPathLexer.g4 and formatted manually.
//
package org.antlr.v5.kotlinruntime.tree.xpath

import org.antlr.v5.kotlinruntime.*
import org.antlr.v5.kotlinruntime.atn.ATN
import org.antlr.v5.kotlinruntime.atn.ATNDeserializer
import org.antlr.v5.kotlinruntime.atn.LexerATNSimulator
import org.antlr.v5.kotlinruntime.atn.PredictionContextCache
import org.antlr.v5.kotlinruntime.dfa.DFA

@Suppress("FunctionName", "LocalVariableName", "ConstPropertyName")
public open class XPathLexer(input: CharStream) : Lexer(input) {
  private companion object {
    private const val SERIALIZED_ATN: String =
      "\u0004\u0000\u0008\u0030\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0005\u0004\u001d\u0008\u0004\u000a\u0004\u000c\u0004\u0020\u0009\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0005\u0005\u0026\u0008\u0005\u000a\u0005\u000c\u0005\u0029\u0009\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0027\u0000\u0008\u0001\u0003\u0003\u0004\u0005\u0005\u0007\u0006\u0009\u0007\u000b\u0008\u000d\u0000\u000f\u0000\u0001\u0000\u0002\u0310\u0000\u0000\u0008\u000e\u001b\u0030\u0039\u0041\u005a\u005f\u005f\u0061\u007a\u007f\u009f\u00aa\u00aa\u00ad\u00ad\u00b5\u00b5\u00ba\u00ba\u00c0\u00d6\u00d8\u00f6\u00f8\u02c1\u02c6\u02d1\u02e0\u02e4\u02ec\u02ec\u02ee\u02ee\u0300\u0374\u0376\u0377\u037a\u037d\u037f\u037f\u0386\u0386\u0388\u038a\u038c\u038c\u038e\u03a1\u03a3\u03f5\u03f7\u0481\u0483\u0487\u048a\u052f\u0531\u0556\u0559\u0559\u0560\u0588\u0591\u05bd\u05bf\u05bf\u05c1\u05c2\u05c4\u05c5\u05c7\u05c7\u05d0\u05ea\u05ef\u05f2\u0600\u0605\u0610\u061a\u061c\u061c\u0620\u0669\u066e\u06d3\u06d5\u06dd\u06df\u06e8\u06ea\u06fc\u06ff\u06ff\u070f\u074a\u074d\u07b1\u07c0\u07f5\u07fa\u07fa\u07fd\u07fd\u0800\u082d\u0840\u085b\u0860\u086a\u0870\u0887\u0889\u088e\u0890\u0891\u0898\u0963\u0966\u096f\u0971\u0983\u0985\u098c\u098f\u0990\u0993\u09a8\u09aa\u09b0\u09b2\u09b2\u09b6\u09b9\u09bc\u09c4\u09c7\u09c8\u09cb\u09ce\u09d7\u09d7\u09dc\u09dd\u09df\u09e3\u09e6\u09f1\u09fc\u09fc\u09fe\u09fe\u0a01\u0a03\u0a05\u0a0a\u0a0f\u0a10\u0a13\u0a28\u0a2a\u0a30\u0a32\u0a33\u0a35\u0a36\u0a38\u0a39\u0a3c\u0a3c\u0a3e\u0a42\u0a47\u0a48\u0a4b\u0a4d\u0a51\u0a51\u0a59\u0a5c\u0a5e\u0a5e\u0a66\u0a75\u0a81\u0a83\u0a85\u0a8d\u0a8f\u0a91\u0a93\u0aa8\u0aaa\u0ab0\u0ab2\u0ab3\u0ab5\u0ab9\u0abc\u0ac5\u0ac7\u0ac9\u0acb\u0acd\u0ad0\u0ad0\u0ae0\u0ae3\u0ae6\u0aef\u0af9\u0aff\u0b01\u0b03\u0b05\u0b0c\u0b0f\u0b10\u0b13\u0b28\u0b2a\u0b30\u0b32\u0b33\u0b35\u0b39\u0b3c\u0b44\u0b47\u0b48\u0b4b\u0b4d\u0b55\u0b57\u0b5c\u0b5d\u0b5f\u0b63\u0b66\u0b6f\u0b71\u0b71\u0b82\u0b83\u0b85\u0b8a\u0b8e\u0b90\u0b92\u0b95\u0b99\u0b9a\u0b9c\u0b9c\u0b9e\u0b9f\u0ba3\u0ba4\u0ba8\u0baa\u0bae\u0bb9\u0bbe\u0bc2\u0bc6\u0bc8\u0bca\u0bcd\u0bd0\u0bd0\u0bd7\u0bd7\u0be6\u0bef\u0c00\u0c0c\u0c0e\u0c10\u0c12\u0c28\u0c2a\u0c39\u0c3c\u0c44\u0c46\u0c48\u0c4a\u0c4d\u0c55\u0c56\u0c58\u0c5a\u0c5d\u0c5d\u0c60\u0c63\u0c66\u0c6f\u0c80\u0c83\u0c85\u0c8c\u0c8e\u0c90\u0c92\u0ca8\u0caa\u0cb3\u0cb5\u0cb9\u0cbc\u0cc4\u0cc6\u0cc8\u0cca\u0ccd\u0cd5\u0cd6\u0cdd\u0cde\u0ce0\u0ce3\u0ce6\u0cef\u0cf1\u0cf3\u0d00\u0d0c\u0d0e\u0d10\u0d12\u0d44\u0d46\u0d48\u0d4a\u0d4e\u0d54\u0d57\u0d5f\u0d63\u0d66\u0d6f\u0d7a\u0d7f\u0d81\u0d83\u0d85\u0d96\u0d9a\u0db1\u0db3\u0dbb\u0dbd\u0dbd\u0dc0\u0dc6\u0dca\u0dca\u0dcf\u0dd4\u0dd6\u0dd6\u0dd8\u0ddf\u0de6\u0def\u0df2\u0df3\u0e01\u0e3a\u0e40\u0e4e\u0e50\u0e59\u0e81\u0e82\u0e84\u0e84\u0e86\u0e8a\u0e8c\u0ea3\u0ea5\u0ea5\u0ea7\u0ebd\u0ec0\u0ec4\u0ec6\u0ec6\u0ec8\u0ece\u0ed0\u0ed9\u0edc\u0edf\u0f00\u0f00\u0f18\u0f19\u0f20\u0f29\u0f35\u0f35\u0f37\u0f37\u0f39\u0f39\u0f3e\u0f47\u0f49\u0f6c\u0f71\u0f84\u0f86\u0f97\u0f99\u0fbc\u0fc6\u0fc6\u1000\u1049\u1050\u109d\u10a0\u10c5\u10c7\u10c7\u10cd\u10cd\u10d0\u10fa\u10fc\u1248\u124a\u124d\u1250\u1256\u1258\u1258\u125a\u125d\u1260\u1288\u128a\u128d\u1290\u12b0\u12b2\u12b5\u12b8\u12be\u12c0\u12c0\u12c2\u12c5\u12c8\u12d6\u12d8\u1310\u1312\u1315\u1318\u135a\u135d\u135f\u1380\u138f\u13a0\u13f5\u13f8\u13fd\u1401\u166c\u166f\u167f\u1681\u169a\u16a0\u16ea\u16ee\u16f8\u1700\u1715\u171f\u1734\u1740\u1753\u1760\u176c\u176e\u1770\u1772\u1773\u1780\u17d3\u17d7\u17d7\u17dc\u17dd\u17e0\u17e9\u180b\u1819\u1820\u1878\u1880\u18aa\u18b0\u18f5\u1900\u191e\u1920\u192b\u1930\u193b\u1946\u196d\u1970\u1974\u1980\u19ab\u19b0\u19c9\u19d0\u19d9\u1a00\u1a1b\u1a20\u1a5e\u1a60\u1a7c\u1a7f\u1a89\u1a90\u1a99\u1aa7\u1aa7\u1ab0\u1abd\u1abf\u1ace\u1b00\u1b4c\u1b50\u1b59\u1b6b\u1b73\u1b80\u1bf3\u1c00\u1c37\u1c40\u1c49\u1c4d\u1c7d\u1c80\u1c88\u1c90\u1cba\u1cbd\u1cbf\u1cd0\u1cd2\u1cd4\u1cfa\u1d00\u1f15\u1f18\u1f1d\u1f20\u1f45\u1f48\u1f4d\u1f50\u1f57\u1f59\u1f59\u1f5b\u1f5b\u1f5d\u1f5d\u1f5f\u1f7d\u1f80\u1fb4\u1fb6\u1fbc\u1fbe\u1fbe\u1fc2\u1fc4\u1fc6\u1fcc\u1fd0\u1fd3\u1fd6\u1fdb\u1fe0\u1fec\u1ff2\u1ff4\u1ff6\u1ffc\u200b\u200f\u202a\u202e\u203f\u2040\u2054\u2054\u2060\u2064\u2066\u206f\u2071\u2071\u207f\u207f\u2090\u209c\u20d0\u20dc\u20e1\u20e1\u20e5\u20f0\u2102\u2102\u2107\u2107\u210a\u2113\u2115\u2115\u2119\u211d\u2124\u2124\u2126\u2126\u2128\u2128\u212a\u212d\u212f\u2139\u213c\u213f\u2145\u2149\u214e\u214e\u2160\u2188\u2c00\u2ce4\u2ceb\u2cf3\u2d00\u2d25\u2d27\u2d27\u2d2d\u2d2d\u2d30\u2d67\u2d6f\u2d6f\u2d7f\u2d96\u2da0\u2da6\u2da8\u2dae\u2db0\u2db6\u2db8\u2dbe\u2dc0\u2dc6\u2dc8\u2dce\u2dd0\u2dd6\u2dd8\u2dde\u2de0\u2dff\u2e2f\u2e2f\u3005\u3007\u3021\u302f\u3031\u3035\u3038\u303c\u3041\u3096\u3099\u309a\u309d\u309f\u30a1\u30fa\u30fc\u30ff\u3105\u312f\u3131\u318e\u31a0\u31bf\u31f0\u31ff\u3400\u4dbf\u4e00\u8000\ua48c\u8000\ua4d0\u8000\ua4fd\u8000\ua500\u8000\ua60c\u8000\ua610\u8000\ua62b\u8000\ua640\u8000\ua66f\u8000\ua674\u8000\ua67d\u8000\ua67f\u8000\ua6f1\u8000\ua717\u8000\ua71f\u8000\ua722\u8000\ua788\u8000\ua78b\u8000\ua7ca\u8000\ua7d0\u8000\ua7d1\u8000\ua7d3\u8000\ua7d3\u8000\ua7d5\u8000\ua7d9\u8000\ua7f2\u8000\ua827\u8000\ua82c\u8000\ua82c\u8000\ua840\u8000\ua873\u8000\ua880\u8000\ua8c5\u8000\ua8d0\u8000\ua8d9\u8000\ua8e0\u8000\ua8f7\u8000\ua8fb\u8000\ua8fb\u8000\ua8fd\u8000\ua92d\u8000\ua930\u8000\ua953\u8000\ua960\u8000\ua97c\u8000\ua980\u8000\ua9c0\u8000\ua9cf\u8000\ua9d9\u8000\ua9e0\u8000\ua9fe\u8000\uaa00\u8000\uaa36\u8000\uaa40\u8000\uaa4d\u8000\uaa50\u8000\uaa59\u8000\uaa60\u8000\uaa76\u8000\uaa7a\u8000\uaac2\u8000\uaadb\u8000\uaadd\u8000\uaae0\u8000\uaaef\u8000\uaaf2\u8000\uaaf6\u8000\uab01\u8000\uab06\u8000\uab09\u8000\uab0e\u8000\uab11\u8000\uab16\u8000\uab20\u8000\uab26\u8000\uab28\u8000\uab2e\u8000\uab30\u8000\uab5a\u8000\uab5c\u8000\uab69\u8000\uab70\u8000\uabea\u8000\uabec\u8000\uabed\u8000\uabf0\u8000\uabf9\u8000\uac00\u8000\ud7a3\u8000\ud7b0\u8000\ud7c6\u8000\ud7cb\u8000\ud7fb\u8000\uf900\u8000\ufa6d\u8000\ufa70\u8000\ufad9\u8000\ufb00\u8000\ufb06\u8000\ufb13\u8000\ufb17\u8000\ufb1d\u8000\ufb28\u8000\ufb2a\u8000\ufb36\u8000\ufb38\u8000\ufb3c\u8000\ufb3e\u8000\ufb3e\u8000\ufb40\u8000\ufb41\u8000\ufb43\u8000\ufb44\u8000\ufb46\u8000\ufbb1\u8000\ufbd3\u8000\ufd3d\u8000\ufd50\u8000\ufd8f\u8000\ufd92\u8000\ufdc7\u8000\ufdf0\u8000\ufdfb\u8000\ufe00\u8000\ufe0f\u8000\ufe20\u8000\ufe2f\u8000\ufe33\u8000\ufe34\u8000\ufe4d\u8000\ufe4f\u8000\ufe70\u8000\ufe74\u8000\ufe76\u8000\ufefc\u8000\ufeff\u8000\ufeff\u8000\uff10\u8000\uff19\u8000\uff21\u8000\uff3a\u8000\uff3f\u8000\uff3f\u8000\uff41\u8000\uff5a\u8000\uff66\u8000\uffbe\u8000\uffc2\u8000\uffc7\u8000\uffca\u8000\uffcf\u8000\uffd2\u8000\uffd7\u8000\uffda\u8000\uffdc\u8000\ufff9\u8000\ufffb\u8001\u0000\u8001\u000b\u8001\u000d\u8001\u0026\u8001\u0028\u8001\u003a\u8001\u003c\u8001\u003d\u8001\u003f\u8001\u004d\u8001\u0050\u8001\u005d\u8001\u0080\u8001\u00fa\u8001\u0140\u8001\u0174\u8001\u01fd\u8001\u01fd\u8001\u0280\u8001\u029c\u8001\u02a0\u8001\u02d0\u8001\u02e0\u8001\u02e0\u8001\u0300\u8001\u031f\u8001\u032d\u8001\u034a\u8001\u0350\u8001\u037a\u8001\u0380\u8001\u039d\u8001\u03a0\u8001\u03c3\u8001\u03c8\u8001\u03cf\u8001\u03d1\u8001\u03d5\u8001\u0400\u8001\u049d\u8001\u04a0\u8001\u04a9\u8001\u04b0\u8001\u04d3\u8001\u04d8\u8001\u04fb\u8001\u0500\u8001\u0527\u8001\u0530\u8001\u0563\u8001\u0570\u8001\u057a\u8001\u057c\u8001\u058a\u8001\u058c\u8001\u0592\u8001\u0594\u8001\u0595\u8001\u0597\u8001\u05a1\u8001\u05a3\u8001\u05b1\u8001\u05b3\u8001\u05b9\u8001\u05bb\u8001\u05bc\u8001\u0600\u8001\u0736\u8001\u0740\u8001\u0755\u8001\u0760\u8001\u0767\u8001\u0780\u8001\u0785\u8001\u0787\u8001\u07b0\u8001\u07b2\u8001\u07ba\u8001\u0800\u8001\u0805\u8001\u0808\u8001\u0808\u8001\u080a\u8001\u0835\u8001\u0837\u8001\u0838\u8001\u083c\u8001\u083c\u8001\u083f\u8001\u0855\u8001\u0860\u8001\u0876\u8001\u0880\u8001\u089e\u8001\u08e0\u8001\u08f2\u8001\u08f4\u8001\u08f5\u8001\u0900\u8001\u0915\u8001\u0920\u8001\u0939\u8001\u0980\u8001\u09b7\u8001\u09be\u8001\u09bf\u8001\u0a00\u8001\u0a03\u8001\u0a05\u8001\u0a06\u8001\u0a0c\u8001\u0a13\u8001\u0a15\u8001\u0a17\u8001\u0a19\u8001\u0a35\u8001\u0a38\u8001\u0a3a\u8001\u0a3f\u8001\u0a3f\u8001\u0a60\u8001\u0a7c\u8001\u0a80\u8001\u0a9c\u8001\u0ac0\u8001\u0ac7\u8001\u0ac9\u8001\u0ae6\u8001\u0b00\u8001\u0b35\u8001\u0b40\u8001\u0b55\u8001\u0b60\u8001\u0b72\u8001\u0b80\u8001\u0b91\u8001\u0c00\u8001\u0c48\u8001\u0c80\u8001\u0cb2\u8001\u0cc0\u8001\u0cf2\u8001\u0d00\u8001\u0d27\u8001\u0d30\u8001\u0d39\u8001\u0e80\u8001\u0ea9\u8001\u0eab\u8001\u0eac\u8001\u0eb0\u8001\u0eb1\u8001\u0efd\u8001\u0f1c\u8001\u0f27\u8001\u0f27\u8001\u0f30\u8001\u0f50\u8001\u0f70\u8001\u0f85\u8001\u0fb0\u8001\u0fc4\u8001\u0fe0\u8001\u0ff6\u8001\u1000\u8001\u1046\u8001\u1066\u8001\u1075\u8001\u107f\u8001\u10ba\u8001\u10bd\u8001\u10bd\u8001\u10c2\u8001\u10c2\u8001\u10cd\u8001\u10cd\u8001\u10d0\u8001\u10e8\u8001\u10f0\u8001\u10f9\u8001\u1100\u8001\u1134\u8001\u1136\u8001\u113f\u8001\u1144\u8001\u1147\u8001\u1150\u8001\u1173\u8001\u1176\u8001\u1176\u8001\u1180\u8001\u11c4\u8001\u11c9\u8001\u11cc\u8001\u11ce\u8001\u11da\u8001\u11dc\u8001\u11dc\u8001\u1200\u8001\u1211\u8001\u1213\u8001\u1237\u8001\u123e\u8001\u1241\u8001\u1280\u8001\u1286\u8001\u1288\u8001\u1288\u8001\u128a\u8001\u128d\u8001\u128f\u8001\u129d\u8001\u129f\u8001\u12a8\u8001\u12b0\u8001\u12ea\u8001\u12f0\u8001\u12f9\u8001\u1300\u8001\u1303\u8001\u1305\u8001\u130c\u8001\u130f\u8001\u1310\u8001\u1313\u8001\u1328\u8001\u132a\u8001\u1330\u8001\u1332\u8001\u1333\u8001\u1335\u8001\u1339\u8001\u133b\u8001\u1344\u8001\u1347\u8001\u1348\u8001\u134b\u8001\u134d\u8001\u1350\u8001\u1350\u8001\u1357\u8001\u1357\u8001\u135d\u8001\u1363\u8001\u1366\u8001\u136c\u8001\u1370\u8001\u1374\u8001\u1400\u8001\u144a\u8001\u1450\u8001\u1459\u8001\u145e\u8001\u1461\u8001\u1480\u8001\u14c5\u8001\u14c7\u8001\u14c7\u8001\u14d0\u8001\u14d9\u8001\u1580\u8001\u15b5\u8001\u15b8\u8001\u15c0\u8001\u15d8\u8001\u15dd\u8001\u1600\u8001\u1640\u8001\u1644\u8001\u1644\u8001\u1650\u8001\u1659\u8001\u1680\u8001\u16b8\u8001\u16c0\u8001\u16c9\u8001\u1700\u8001\u171a\u8001\u171d\u8001\u172b\u8001\u1730\u8001\u1739\u8001\u1740\u8001\u1746\u8001\u1800\u8001\u183a\u8001\u18a0\u8001\u18e9\u8001\u18ff\u8001\u1906\u8001\u1909\u8001\u1909\u8001\u190c\u8001\u1913\u8001\u1915\u8001\u1916\u8001\u1918\u8001\u1935\u8001\u1937\u8001\u1938\u8001\u193b\u8001\u1943\u8001\u1950\u8001\u1959\u8001\u19a0\u8001\u19a7\u8001\u19aa\u8001\u19d7\u8001\u19da\u8001\u19e1\u8001\u19e3\u8001\u19e4\u8001\u1a00\u8001\u1a3e\u8001\u1a47\u8001\u1a47\u8001\u1a50\u8001\u1a99\u8001\u1a9d\u8001\u1a9d\u8001\u1ab0\u8001\u1af8\u8001\u1c00\u8001\u1c08\u8001\u1c0a\u8001\u1c36\u8001\u1c38\u8001\u1c40\u8001\u1c50\u8001\u1c59\u8001\u1c72\u8001\u1c8f\u8001\u1c92\u8001\u1ca7\u8001\u1ca9\u8001\u1cb6\u8001\u1d00\u8001\u1d06\u8001\u1d08\u8001\u1d09\u8001\u1d0b\u8001\u1d36\u8001\u1d3a\u8001\u1d3a\u8001\u1d3c\u8001\u1d3d\u8001\u1d3f\u8001\u1d47\u8001\u1d50\u8001\u1d59\u8001\u1d60\u8001\u1d65\u8001\u1d67\u8001\u1d68\u8001\u1d6a\u8001\u1d8e\u8001\u1d90\u8001\u1d91\u8001\u1d93\u8001\u1d98\u8001\u1da0\u8001\u1da9\u8001\u1ee0\u8001\u1ef6\u8001\u1f00\u8001\u1f10\u8001\u1f12\u8001\u1f3a\u8001\u1f3e\u8001\u1f42\u8001\u1f50\u8001\u1f59\u8001\u1fb0\u8001\u1fb0\u8001\u2000\u8001\u2399\u8001\u2400\u8001\u246e\u8001\u2480\u8001\u2543\u8001\u2f90\u8001\u2ff0\u8001\u3000\u8001\u3455\u8001\u4400\u8001\u4646\u8001\u6800\u8001\u6a38\u8001\u6a40\u8001\u6a5e\u8001\u6a60\u8001\u6a69\u8001\u6a70\u8001\u6abe\u8001\u6ac0\u8001\u6ac9\u8001\u6ad0\u8001\u6aed\u8001\u6af0\u8001\u6af4\u8001\u6b00\u8001\u6b36\u8001\u6b40\u8001\u6b43\u8001\u6b50\u8001\u6b59\u8001\u6b63\u8001\u6b77\u8001\u6b7d\u8001\u6b8f\u8001\u6e40\u8001\u6e7f\u8001\u6f00\u8001\u6f4a\u8001\u6f4f\u8001\u6f87\u8001\u6f8f\u8001\u6f9f\u8001\u6fe0\u8001\u6fe1\u8001\u6fe3\u8001\u6fe4\u8001\u6ff0\u8001\u6ff1\u8001\u7000\u8001\u87f7\u8001\u8800\u8001\u8cd5\u8001\u8d00\u8001\u8d08\u8001\uaff0\u8001\uaff3\u8001\uaff5\u8001\uaffb\u8001\uaffd\u8001\uaffe\u8001\ub000\u8001\ub122\u8001\ub132\u8001\ub132\u8001\ub150\u8001\ub152\u8001\ub155\u8001\ub155\u8001\ub164\u8001\ub167\u8001\ub170\u8001\ub2fb\u8001\ubc00\u8001\ubc6a\u8001\ubc70\u8001\ubc7c\u8001\ubc80\u8001\ubc88\u8001\ubc90\u8001\ubc99\u8001\ubc9d\u8001\ubc9e\u8001\ubca0\u8001\ubca3\u8001\ucf00\u8001\ucf2d\u8001\ucf30\u8001\ucf46\u8001\ud165\u8001\ud169\u8001\ud16d\u8001\ud182\u8001\ud185\u8001\ud18b\u8001\ud1aa\u8001\ud1ad\u8001\ud242\u8001\ud244\u8001\ud400\u8001\ud454\u8001\ud456\u8001\ud49c\u8001\ud49e\u8001\ud49f\u8001\ud4a2\u8001\ud4a2\u8001\ud4a5\u8001\ud4a6\u8001\ud4a9\u8001\ud4ac\u8001\ud4ae\u8001\ud4b9\u8001\ud4bb\u8001\ud4bb\u8001\ud4bd\u8001\ud4c3\u8001\ud4c5\u8001\ud505\u8001\ud507\u8001\ud50a\u8001\ud50d\u8001\ud514\u8001\ud516\u8001\ud51c\u8001\ud51e\u8001\ud539\u8001\ud53b\u8001\ud53e\u8001\ud540\u8001\ud544\u8001\ud546\u8001\ud546\u8001\ud54a\u8001\ud550\u8001\ud552\u8001\ud6a5\u8001\ud6a8\u8001\ud6c0\u8001\ud6c2\u8001\ud6da\u8001\ud6dc\u8001\ud6fa\u8001\ud6fc\u8001\ud714\u8001\ud716\u8001\ud734\u8001\ud736\u8001\ud74e\u8001\ud750\u8001\ud76e\u8001\ud770\u8001\ud788\u8001\ud78a\u8001\ud7a8\u8001\ud7aa\u8001\ud7c2\u8001\ud7c4\u8001\ud7cb\u8001\ud7ce\u8001\ud7ff\u8001\uda00\u8001\uda36\u8001\uda3b\u8001\uda6c\u8001\uda75\u8001\uda75\u8001\uda84\u8001\uda84\u8001\uda9b\u8001\uda9f\u8001\udaa1\u8001\udaaf\u8001\udf00\u8001\udf1e\u8001\udf25\u8001\udf2a\u8001\ue000\u8001\ue006\u8001\ue008\u8001\ue018\u8001\ue01b\u8001\ue021\u8001\ue023\u8001\ue024\u8001\ue026\u8001\ue02a\u8001\ue030\u8001\ue06d\u8001\ue08f\u8001\ue08f\u8001\ue100\u8001\ue12c\u8001\ue130\u8001\ue13d\u8001\ue140\u8001\ue149\u8001\ue14e\u8001\ue14e\u8001\ue290\u8001\ue2ae\u8001\ue2c0\u8001\ue2f9\u8001\ue4d0\u8001\ue4f9\u8001\ue7e0\u8001\ue7e6\u8001\ue7e8\u8001\ue7eb\u8001\ue7ed\u8001\ue7ee\u8001\ue7f0\u8001\ue7fe\u8001\ue800\u8001\ue8c4\u8001\ue8d0\u8001\ue8d6\u8001\ue900\u8001\ue94b\u8001\ue950\u8001\ue959\u8001\uee00\u8001\uee03\u8001\uee05\u8001\uee1f\u8001\uee21\u8001\uee22\u8001\uee24\u8001\uee24\u8001\uee27\u8001\uee27\u8001\uee29\u8001\uee32\u8001\uee34\u8001\uee37\u8001\uee39\u8001\uee39\u8001\uee3b\u8001\uee3b\u8001\uee42\u8001\uee42\u8001\uee47\u8001\uee47\u8001\uee49\u8001\uee49\u8001\uee4b\u8001\uee4b\u8001\uee4d\u8001\uee4f\u8001\uee51\u8001\uee52\u8001\uee54\u8001\uee54\u8001\uee57\u8001\uee57\u8001\uee59\u8001\uee59\u8001\uee5b\u8001\uee5b\u8001\uee5d\u8001\uee5d\u8001\uee5f\u8001\uee5f\u8001\uee61\u8001\uee62\u8001\uee64\u8001\uee64\u8001\uee67\u8001\uee6a\u8001\uee6c\u8001\uee72\u8001\uee74\u8001\uee77\u8001\uee79\u8001\uee7c\u8001\uee7e\u8001\uee7e\u8001\uee80\u8001\uee89\u8001\uee8b\u8001\uee9b\u8001\ueea1\u8001\ueea3\u8001\ueea5\u8001\ueea9\u8001\ueeab\u8001\ueebb\u8001\ufbf0\u8001\ufbf9\u8002\u0000\u8002\ua6df\u8002\ua700\u8002\ub739\u8002\ub740\u8002\ub81d\u8002\ub820\u8002\ucea1\u8002\uceb0\u8002\uebe0\u8002\uf800\u8002\ufa1d\u8003\u0000\u8003\u134a\u8003\u1350\u8003\u23af\u800e\u0001\u800e\u0001\u800e\u0020\u800e\u007f\u800e\u0100\u800e\u01ef\u0296\u0000\u0041\u005a\u0061\u007a\u00aa\u00aa\u00b5\u00b5\u00ba\u00ba\u00c0\u00d6\u00d8\u00f6\u00f8\u02c1\u02c6\u02d1\u02e0\u02e4\u02ec\u02ec\u02ee\u02ee\u0370\u0374\u0376\u0377\u037a\u037d\u037f\u037f\u0386\u0386\u0388\u038a\u038c\u038c\u038e\u03a1\u03a3\u03f5\u03f7\u0481\u048a\u052f\u0531\u0556\u0559\u0559\u0560\u0588\u05d0\u05ea\u05ef\u05f2\u0620\u064a\u066e\u066f\u0671\u06d3\u06d5\u06d5\u06e5\u06e6\u06ee\u06ef\u06fa\u06fc\u06ff\u06ff\u0710\u0710\u0712\u072f\u074d\u07a5\u07b1\u07b1\u07ca\u07ea\u07f4\u07f5\u07fa\u07fa\u0800\u0815\u081a\u081a\u0824\u0824\u0828\u0828\u0840\u0858\u0860\u086a\u0870\u0887\u0889\u088e\u08a0\u08c9\u0904\u0939\u093d\u093d\u0950\u0950\u0958\u0961\u0971\u0980\u0985\u098c\u098f\u0990\u0993\u09a8\u09aa\u09b0\u09b2\u09b2\u09b6\u09b9\u09bd\u09bd\u09ce\u09ce\u09dc\u09dd\u09df\u09e1\u09f0\u09f1\u09fc\u09fc\u0a05\u0a0a\u0a0f\u0a10\u0a13\u0a28\u0a2a\u0a30\u0a32\u0a33\u0a35\u0a36\u0a38\u0a39\u0a59\u0a5c\u0a5e\u0a5e\u0a72\u0a74\u0a85\u0a8d\u0a8f\u0a91\u0a93\u0aa8\u0aaa\u0ab0\u0ab2\u0ab3\u0ab5\u0ab9\u0abd\u0abd\u0ad0\u0ad0\u0ae0\u0ae1\u0af9\u0af9\u0b05\u0b0c\u0b0f\u0b10\u0b13\u0b28\u0b2a\u0b30\u0b32\u0b33\u0b35\u0b39\u0b3d\u0b3d\u0b5c\u0b5d\u0b5f\u0b61\u0b71\u0b71\u0b83\u0b83\u0b85\u0b8a\u0b8e\u0b90\u0b92\u0b95\u0b99\u0b9a\u0b9c\u0b9c\u0b9e\u0b9f\u0ba3\u0ba4\u0ba8\u0baa\u0bae\u0bb9\u0bd0\u0bd0\u0c05\u0c0c\u0c0e\u0c10\u0c12\u0c28\u0c2a\u0c39\u0c3d\u0c3d\u0c58\u0c5a\u0c5d\u0c5d\u0c60\u0c61\u0c80\u0c80\u0c85\u0c8c\u0c8e\u0c90\u0c92\u0ca8\u0caa\u0cb3\u0cb5\u0cb9\u0cbd\u0cbd\u0cdd\u0cde\u0ce0\u0ce1\u0cf1\u0cf2\u0d04\u0d0c\u0d0e\u0d10\u0d12\u0d3a\u0d3d\u0d3d\u0d4e\u0d4e\u0d54\u0d56\u0d5f\u0d61\u0d7a\u0d7f\u0d85\u0d96\u0d9a\u0db1\u0db3\u0dbb\u0dbd\u0dbd\u0dc0\u0dc6\u0e01\u0e30\u0e32\u0e33\u0e40\u0e46\u0e81\u0e82\u0e84\u0e84\u0e86\u0e8a\u0e8c\u0ea3\u0ea5\u0ea5\u0ea7\u0eb0\u0eb2\u0eb3\u0ebd\u0ebd\u0ec0\u0ec4\u0ec6\u0ec6\u0edc\u0edf\u0f00\u0f00\u0f40\u0f47\u0f49\u0f6c\u0f88\u0f8c\u1000\u102a\u103f\u103f\u1050\u1055\u105a\u105d\u1061\u1061\u1065\u1066\u106e\u1070\u1075\u1081\u108e\u108e\u10a0\u10c5\u10c7\u10c7\u10cd\u10cd\u10d0\u10fa\u10fc\u1248\u124a\u124d\u1250\u1256\u1258\u1258\u125a\u125d\u1260\u1288\u128a\u128d\u1290\u12b0\u12b2\u12b5\u12b8\u12be\u12c0\u12c0\u12c2\u12c5\u12c8\u12d6\u12d8\u1310\u1312\u1315\u1318\u135a\u1380\u138f\u13a0\u13f5\u13f8\u13fd\u1401\u166c\u166f\u167f\u1681\u169a\u16a0\u16ea\u16ee\u16f8\u1700\u1711\u171f\u1731\u1740\u1751\u1760\u176c\u176e\u1770\u1780\u17b3\u17d7\u17d7\u17dc\u17dc\u1820\u1878\u1880\u1884\u1887\u18a8\u18aa\u18aa\u18b0\u18f5\u1900\u191e\u1950\u196d\u1970\u1974\u1980\u19ab\u19b0\u19c9\u1a00\u1a16\u1a20\u1a54\u1aa7\u1aa7\u1b05\u1b33\u1b45\u1b4c\u1b83\u1ba0\u1bae\u1baf\u1bba\u1be5\u1c00\u1c23\u1c4d\u1c4f\u1c5a\u1c7d\u1c80\u1c88\u1c90\u1cba\u1cbd\u1cbf\u1ce9\u1cec\u1cee\u1cf3\u1cf5\u1cf6\u1cfa\u1cfa\u1d00\u1dbf\u1e00\u1f15\u1f18\u1f1d\u1f20\u1f45\u1f48\u1f4d\u1f50\u1f57\u1f59\u1f59\u1f5b\u1f5b\u1f5d\u1f5d\u1f5f\u1f7d\u1f80\u1fb4\u1fb6\u1fbc\u1fbe\u1fbe\u1fc2\u1fc4\u1fc6\u1fcc\u1fd0\u1fd3\u1fd6\u1fdb\u1fe0\u1fec\u1ff2\u1ff4\u1ff6\u1ffc\u2071\u2071\u207f\u207f\u2090\u209c\u2102\u2102\u2107\u2107\u210a\u2113\u2115\u2115\u2119\u211d\u2124\u2124\u2126\u2126\u2128\u2128\u212a\u212d\u212f\u2139\u213c\u213f\u2145\u2149\u214e\u214e\u2160\u2188\u2c00\u2ce4\u2ceb\u2cee\u2cf2\u2cf3\u2d00\u2d25\u2d27\u2d27\u2d2d\u2d2d\u2d30\u2d67\u2d6f\u2d6f\u2d80\u2d96\u2da0\u2da6\u2da8\u2dae\u2db0\u2db6\u2db8\u2dbe\u2dc0\u2dc6\u2dc8\u2dce\u2dd0\u2dd6\u2dd8\u2dde\u2e2f\u2e2f\u3005\u3007\u3021\u3029\u3031\u3035\u3038\u303c\u3041\u3096\u309d\u309f\u30a1\u30fa\u30fc\u30ff\u3105\u312f\u3131\u318e\u31a0\u31bf\u31f0\u31ff\u3400\u4dbf\u4e00\u8000\ua48c\u8000\ua4d0\u8000\ua4fd\u8000\ua500\u8000\ua60c\u8000\ua610\u8000\ua61f\u8000\ua62a\u8000\ua62b\u8000\ua640\u8000\ua66e\u8000\ua67f\u8000\ua69d\u8000\ua6a0\u8000\ua6ef\u8000\ua717\u8000\ua71f\u8000\ua722\u8000\ua788\u8000\ua78b\u8000\ua7ca\u8000\ua7d0\u8000\ua7d1\u8000\ua7d3\u8000\ua7d3\u8000\ua7d5\u8000\ua7d9\u8000\ua7f2\u8000\ua801\u8000\ua803\u8000\ua805\u8000\ua807\u8000\ua80a\u8000\ua80c\u8000\ua822\u8000\ua840\u8000\ua873\u8000\ua882\u8000\ua8b3\u8000\ua8f2\u8000\ua8f7\u8000\ua8fb\u8000\ua8fb\u8000\ua8fd\u8000\ua8fe\u8000\ua90a\u8000\ua925\u8000\ua930\u8000\ua946\u8000\ua960\u8000\ua97c\u8000\ua984\u8000\ua9b2\u8000\ua9cf\u8000\ua9cf\u8000\ua9e0\u8000\ua9e4\u8000\ua9e6\u8000\ua9ef\u8000\ua9fa\u8000\ua9fe\u8000\uaa00\u8000\uaa28\u8000\uaa40\u8000\uaa42\u8000\uaa44\u8000\uaa4b\u8000\uaa60\u8000\uaa76\u8000\uaa7a\u8000\uaa7a\u8000\uaa7e\u8000\uaaaf\u8000\uaab1\u8000\uaab1\u8000\uaab5\u8000\uaab6\u8000\uaab9\u8000\uaabd\u8000\uaac0\u8000\uaac0\u8000\uaac2\u8000\uaac2\u8000\uaadb\u8000\uaadd\u8000\uaae0\u8000\uaaea\u8000\uaaf2\u8000\uaaf4\u8000\uab01\u8000\uab06\u8000\uab09\u8000\uab0e\u8000\uab11\u8000\uab16\u8000\uab20\u8000\uab26\u8000\uab28\u8000\uab2e\u8000\uab30\u8000\uab5a\u8000\uab5c\u8000\uab69\u8000\uab70\u8000\uabe2\u8000\uac00\u8000\ud7a3\u8000\ud7b0\u8000\ud7c6\u8000\ud7cb\u8000\ud7fb\u8000\uf900\u8000\ufa6d\u8000\ufa70\u8000\ufad9\u8000\ufb00\u8000\ufb06\u8000\ufb13\u8000\ufb17\u8000\ufb1d\u8000\ufb1d\u8000\ufb1f\u8000\ufb28\u8000\ufb2a\u8000\ufb36\u8000\ufb38\u8000\ufb3c\u8000\ufb3e\u8000\ufb3e\u8000\ufb40\u8000\ufb41\u8000\ufb43\u8000\ufb44\u8000\ufb46\u8000\ufbb1\u8000\ufbd3\u8000\ufd3d\u8000\ufd50\u8000\ufd8f\u8000\ufd92\u8000\ufdc7\u8000\ufdf0\u8000\ufdfb\u8000\ufe70\u8000\ufe74\u8000\ufe76\u8000\ufefc\u8000\uff21\u8000\uff3a\u8000\uff41\u8000\uff5a\u8000\uff66\u8000\uffbe\u8000\uffc2\u8000\uffc7\u8000\uffca\u8000\uffcf\u8000\uffd2\u8000\uffd7\u8000\uffda\u8000\uffdc\u8001\u0000\u8001\u000b\u8001\u000d\u8001\u0026\u8001\u0028\u8001\u003a\u8001\u003c\u8001\u003d\u8001\u003f\u8001\u004d\u8001\u0050\u8001\u005d\u8001\u0080\u8001\u00fa\u8001\u0140\u8001\u0174\u8001\u0280\u8001\u029c\u8001\u02a0\u8001\u02d0\u8001\u0300\u8001\u031f\u8001\u032d\u8001\u034a\u8001\u0350\u8001\u0375\u8001\u0380\u8001\u039d\u8001\u03a0\u8001\u03c3\u8001\u03c8\u8001\u03cf\u8001\u03d1\u8001\u03d5\u8001\u0400\u8001\u049d\u8001\u04b0\u8001\u04d3\u8001\u04d8\u8001\u04fb\u8001\u0500\u8001\u0527\u8001\u0530\u8001\u0563\u8001\u0570\u8001\u057a\u8001\u057c\u8001\u058a\u8001\u058c\u8001\u0592\u8001\u0594\u8001\u0595\u8001\u0597\u8001\u05a1\u8001\u05a3\u8001\u05b1\u8001\u05b3\u8001\u05b9\u8001\u05bb\u8001\u05bc\u8001\u0600\u8001\u0736\u8001\u0740\u8001\u0755\u8001\u0760\u8001\u0767\u8001\u0780\u8001\u0785\u8001\u0787\u8001\u07b0\u8001\u07b2\u8001\u07ba\u8001\u0800\u8001\u0805\u8001\u0808\u8001\u0808\u8001\u080a\u8001\u0835\u8001\u0837\u8001\u0838\u8001\u083c\u8001\u083c\u8001\u083f\u8001\u0855\u8001\u0860\u8001\u0876\u8001\u0880\u8001\u089e\u8001\u08e0\u8001\u08f2\u8001\u08f4\u8001\u08f5\u8001\u0900\u8001\u0915\u8001\u0920\u8001\u0939\u8001\u0980\u8001\u09b7\u8001\u09be\u8001\u09bf\u8001\u0a00\u8001\u0a00\u8001\u0a10\u8001\u0a13\u8001\u0a15\u8001\u0a17\u8001\u0a19\u8001\u0a35\u8001\u0a60\u8001\u0a7c\u8001\u0a80\u8001\u0a9c\u8001\u0ac0\u8001\u0ac7\u8001\u0ac9\u8001\u0ae4\u8001\u0b00\u8001\u0b35\u8001\u0b40\u8001\u0b55\u8001\u0b60\u8001\u0b72\u8001\u0b80\u8001\u0b91\u8001\u0c00\u8001\u0c48\u8001\u0c80\u8001\u0cb2\u8001\u0cc0\u8001\u0cf2\u8001\u0d00\u8001\u0d23\u8001\u0e80\u8001\u0ea9\u8001\u0eb0\u8001\u0eb1\u8001\u0f00\u8001\u0f1c\u8001\u0f27\u8001\u0f27\u8001\u0f30\u8001\u0f45\u8001\u0f70\u8001\u0f81\u8001\u0fb0\u8001\u0fc4\u8001\u0fe0\u8001\u0ff6\u8001\u1003\u8001\u1037\u8001\u1071\u8001\u1072\u8001\u1075\u8001\u1075\u8001\u1083\u8001\u10af\u8001\u10d0\u8001\u10e8\u8001\u1103\u8001\u1126\u8001\u1144\u8001\u1144\u8001\u1147\u8001\u1147\u8001\u1150\u8001\u1172\u8001\u1176\u8001\u1176\u8001\u1183\u8001\u11b2\u8001\u11c1\u8001\u11c4\u8001\u11da\u8001\u11da\u8001\u11dc\u8001\u11dc\u8001\u1200\u8001\u1211\u8001\u1213\u8001\u122b\u8001\u123f\u8001\u1240\u8001\u1280\u8001\u1286\u8001\u1288\u8001\u1288\u8001\u128a\u8001\u128d\u8001\u128f\u8001\u129d\u8001\u129f\u8001\u12a8\u8001\u12b0\u8001\u12de\u8001\u1305\u8001\u130c\u8001\u130f\u8001\u1310\u8001\u1313\u8001\u1328\u8001\u132a\u8001\u1330\u8001\u1332\u8001\u1333\u8001\u1335\u8001\u1339\u8001\u133d\u8001\u133d\u8001\u1350\u8001\u1350\u8001\u135d\u8001\u1361\u8001\u1400\u8001\u1434\u8001\u1447\u8001\u144a\u8001\u145f\u8001\u1461\u8001\u1480\u8001\u14af\u8001\u14c4\u8001\u14c5\u8001\u14c7\u8001\u14c7\u8001\u1580\u8001\u15ae\u8001\u15d8\u8001\u15db\u8001\u1600\u8001\u162f\u8001\u1644\u8001\u1644\u8001\u1680\u8001\u16aa\u8001\u16b8\u8001\u16b8\u8001\u1700\u8001\u171a\u8001\u1740\u8001\u1746\u8001\u1800\u8001\u182b\u8001\u18a0\u8001\u18df\u8001\u18ff\u8001\u1906\u8001\u1909\u8001\u1909\u8001\u190c\u8001\u1913\u8001\u1915\u8001\u1916\u8001\u1918\u8001\u192f\u8001\u193f\u8001\u193f\u8001\u1941\u8001\u1941\u8001\u19a0\u8001\u19a7\u8001\u19aa\u8001\u19d0\u8001\u19e1\u8001\u19e1\u8001\u19e3\u8001\u19e3\u8001\u1a00\u8001\u1a00\u8001\u1a0b\u8001\u1a32\u8001\u1a3a\u8001\u1a3a\u8001\u1a50\u8001\u1a50\u8001\u1a5c\u8001\u1a89\u8001\u1a9d\u8001\u1a9d\u8001\u1ab0\u8001\u1af8\u8001\u1c00\u8001\u1c08\u8001\u1c0a\u8001\u1c2e\u8001\u1c40\u8001\u1c40\u8001\u1c72\u8001\u1c8f\u8001\u1d00\u8001\u1d06\u8001\u1d08\u8001\u1d09\u8001\u1d0b\u8001\u1d30\u8001\u1d46\u8001\u1d46\u8001\u1d60\u8001\u1d65\u8001\u1d67\u8001\u1d68\u8001\u1d6a\u8001\u1d89\u8001\u1d98\u8001\u1d98\u8001\u1ee0\u8001\u1ef2\u8001\u1f02\u8001\u1f02\u8001\u1f04\u8001\u1f10\u8001\u1f12\u8001\u1f33\u8001\u1fb0\u8001\u1fb0\u8001\u2000\u8001\u2399\u8001\u2400\u8001\u246e\u8001\u2480\u8001\u2543\u8001\u2f90\u8001\u2ff0\u8001\u3000\u8001\u342f\u8001\u3441\u8001\u3446\u8001\u4400\u8001\u4646\u8001\u6800\u8001\u6a38\u8001\u6a40\u8001\u6a5e\u8001\u6a70\u8001\u6abe\u8001\u6ad0\u8001\u6aed\u8001\u6b00\u8001\u6b2f\u8001\u6b40\u8001\u6b43\u8001\u6b63\u8001\u6b77\u8001\u6b7d\u8001\u6b8f\u8001\u6e40\u8001\u6e7f\u8001\u6f00\u8001\u6f4a\u8001\u6f50\u8001\u6f50\u8001\u6f93\u8001\u6f9f\u8001\u6fe0\u8001\u6fe1\u8001\u6fe3\u8001\u6fe3\u8001\u7000\u8001\u87f7\u8001\u8800\u8001\u8cd5\u8001\u8d00\u8001\u8d08\u8001\uaff0\u8001\uaff3\u8001\uaff5\u8001\uaffb\u8001\uaffd\u8001\uaffe\u8001\ub000\u8001\ub122\u8001\ub132\u8001\ub132\u8001\ub150\u8001\ub152\u8001\ub155\u8001\ub155\u8001\ub164\u8001\ub167\u8001\ub170\u8001\ub2fb\u8001\ubc00\u8001\ubc6a\u8001\ubc70\u8001\ubc7c\u8001\ubc80\u8001\ubc88\u8001\ubc90\u8001\ubc99\u8001\ud400\u8001\ud454\u8001\ud456\u8001\ud49c\u8001\ud49e\u8001\ud49f\u8001\ud4a2\u8001\ud4a2\u8001\ud4a5\u8001\ud4a6\u8001\ud4a9\u8001\ud4ac\u8001\ud4ae\u8001\ud4b9\u8001\ud4bb\u8001\ud4bb\u8001\ud4bd\u8001\ud4c3\u8001\ud4c5\u8001\ud505\u8001\ud507\u8001\ud50a\u8001\ud50d\u8001\ud514\u8001\ud516\u8001\ud51c\u8001\ud51e\u8001\ud539\u8001\ud53b\u8001\ud53e\u8001\ud540\u8001\ud544\u8001\ud546\u8001\ud546\u8001\ud54a\u8001\ud550\u8001\ud552\u8001\ud6a5\u8001\ud6a8\u8001\ud6c0\u8001\ud6c2\u8001\ud6da\u8001\ud6dc\u8001\ud6fa\u8001\ud6fc\u8001\ud714\u8001\ud716\u8001\ud734\u8001\ud736\u8001\ud74e\u8001\ud750\u8001\ud76e\u8001\ud770\u8001\ud788\u8001\ud78a\u8001\ud7a8\u8001\ud7aa\u8001\ud7c2\u8001\ud7c4\u8001\ud7cb\u8001\udf00\u8001\udf1e\u8001\udf25\u8001\udf2a\u8001\ue030\u8001\ue06d\u8001\ue100\u8001\ue12c\u8001\ue137\u8001\ue13d\u8001\ue14e\u8001\ue14e\u8001\ue290\u8001\ue2ad\u8001\ue2c0\u8001\ue2eb\u8001\ue4d0\u8001\ue4eb\u8001\ue7e0\u8001\ue7e6\u8001\ue7e8\u8001\ue7eb\u8001\ue7ed\u8001\ue7ee\u8001\ue7f0\u8001\ue7fe\u8001\ue800\u8001\ue8c4\u8001\ue900\u8001\ue943\u8001\ue94b\u8001\ue94b\u8001\uee00\u8001\uee03\u8001\uee05\u8001\uee1f\u8001\uee21\u8001\uee22\u8001\uee24\u8001\uee24\u8001\uee27\u8001\uee27\u8001\uee29\u8001\uee32\u8001\uee34\u8001\uee37\u8001\uee39\u8001\uee39\u8001\uee3b\u8001\uee3b\u8001\uee42\u8001\uee42\u8001\uee47\u8001\uee47\u8001\uee49\u8001\uee49\u8001\uee4b\u8001\uee4b\u8001\uee4d\u8001\uee4f\u8001\uee51\u8001\uee52\u8001\uee54\u8001\uee54\u8001\uee57\u8001\uee57\u8001\uee59\u8001\uee59\u8001\uee5b\u8001\uee5b\u8001\uee5d\u8001\uee5d\u8001\uee5f\u8001\uee5f\u8001\uee61\u8001\uee62\u8001\uee64\u8001\uee64\u8001\uee67\u8001\uee6a\u8001\uee6c\u8001\uee72\u8001\uee74\u8001\uee77\u8001\uee79\u8001\uee7c\u8001\uee7e\u8001\uee7e\u8001\uee80\u8001\uee89\u8001\uee8b\u8001\uee9b\u8001\ueea1\u8001\ueea3\u8001\ueea5\u8001\ueea9\u8001\ueeab\u8001\ueebb\u8002\u0000\u8002\ua6df\u8002\ua700\u8002\ub739\u8002\ub740\u8002\ub81d\u8002\ub820\u8002\ucea1\u8002\uceb0\u8002\uebe0\u8002\uf800\u8002\ufa1d\u8003\u0000\u8003\u134a\u8003\u1350\u8003\u23af\u002f\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\u0009\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0001\u0011\u0001\u0000\u0000\u0000\u0003\u0014\u0001\u0000\u0000\u0000\u0005\u0016\u0001\u0000\u0000\u0000\u0007\u0018\u0001\u0000\u0000\u0000\u0009\u001a\u0001\u0000\u0000\u0000\u000b\u0023\u0001\u0000\u0000\u0000\u000d\u002c\u0001\u0000\u0000\u0000\u000f\u002e\u0001\u0000\u0000\u0000\u0011\u0012\u0005\u002f\u0000\u0000\u0012\u0013\u0005\u002f\u0000\u0000\u0013\u0002\u0001\u0000\u0000\u0000\u0014\u0015\u0005\u002f\u0000\u0000\u0015\u0004\u0001\u0000\u0000\u0000\u0016\u0017\u0005\u002a\u0000\u0000\u0017\u0006\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0021\u0000\u0000\u0019\u0008\u0001\u0000\u0000\u0000\u001a\u001e\u0003\u000f\u0007\u0000\u001b\u001d\u0003\u000d\u0006\u0000\u001c\u001b\u0001\u0000\u0000\u0000\u001d\u0020\u0001\u0000\u0000\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f\u0021\u0001\u0000\u0000\u0000\u0020\u001e\u0001\u0000\u0000\u0000\u0021\u0022\u0006\u0004\u0000\u0000\u0022\u000a\u0001\u0000\u0000\u0000\u0023\u0027\u0005\u0027\u0000\u0000\u0024\u0026\u0009\u0000\u0000\u0000\u0025\u0024\u0001\u0000\u0000\u0000\u0026\u0029\u0001\u0000\u0000\u0000\u0027\u0028\u0001\u0000\u0000\u0000\u0027\u0025\u0001\u0000\u0000\u0000\u0028\u002a\u0001\u0000\u0000\u0000\u0029\u0027\u0001\u0000\u0000\u0000\u002a\u002b\u0005\u0027\u0000\u0000\u002b\u000c\u0001\u0000\u0000\u0000\u002c\u002d\u0007\u0000\u0000\u0000\u002d\u000e\u0001\u0000\u0000\u0000\u002e\u002f\u0007\u0001\u0000\u0000\u002f\u0010\u0001\u0000\u0000\u0000\u0003\u0000\u001e\u0027\u0001\u0001\u0004\u0000"

    private val ATN = ATNDeserializer().deserialize(SERIALIZED_ATN.toCharArray())

    private val DECISION_TO_DFA = Array(ATN.numberOfDecisions) {
      DFA(ATN.getDecisionState(it)!!, it)
    }

    private val SHARED_CONTEXT_CACHE = PredictionContextCache()

    private val LITERAL_NAMES: Array<String?> = arrayOf(
      null, null, null, "'//'", "'/'", "'*'", "'!'"
    )

    private val SYMBOLIC_NAMES: Array<String?> = arrayOf(
      null, "TokenRef", "RuleRef", "Anywhere", "Root", "Wildcard", "Bang", "Id", "String"
    )

    private val VOCABULARY = VocabularyImpl(LITERAL_NAMES, SYMBOLIC_NAMES)
  }

  public object Tokens {
    public const val TokenRef: Int = 1
    public const val RuleRef: Int = 2
    public const val Anywhere: Int = 3
    public const val Root: Int = 4
    public const val Wildcard: Int = 5
    public const val Bang: Int = 6
    public const val Id: Int = 7
    public const val String: Int = 8
  }

  public object Channels {
    public const val DEFAULT_TOKEN_CHANNEL: Int = 0
    public const val HIDDEN: Int = 1
  }

  public object Modes {
    public const val DEFAULT_MODE: Int = 0
  }

  override var interpreter: LexerATNSimulator =
    @Suppress("LeakingThis")
    LexerATNSimulator(this, ATN, DECISION_TO_DFA, SHARED_CONTEXT_CACHE)

  override val grammarFileName: String =
    "XPathLexer.g4"

  override val atn: ATN =
    ATN

  override val vocabulary: Vocabulary =
    VOCABULARY

  override val serializedATN: String =
    SERIALIZED_ATN

  override val ruleNames: Array<String> = arrayOf(
    "Anywhere",
    "Root",
    "Wildcard",
    "Bang",
    "Id",
    "String",
    "NAME_CHAR",
    "NAME_START_CHAR",
  )

  override val channelNames: Array<String> = arrayOf(
    "DEFAULT_TOKEN_CHANNEL",
    "HIDDEN",
  )

  override val modeNames: Array<String> = arrayOf(
    "DEFAULT_MODE",
  )

  override fun action(_localctx: RuleContext?, ruleIndex: Int, actionIndex: Int) {
    when (ruleIndex) {
      4 -> Id_action(_localctx, actionIndex)
    }
  }

  @Suppress("UNUSED_PARAMETER")
  private fun Id_action(_localctx: RuleContext?, actionIndex: Int) {
    when (actionIndex) {
      0 -> {
        val text = this.text

        type = if (text[0].isUpperCase()) {
          Tokens.TokenRef
        } else {
          Tokens.RuleRef
        }
      }
    }
  }
}